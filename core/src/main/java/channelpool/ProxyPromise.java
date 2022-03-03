package channelpool;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProxyPromise<C> {
	protected Promise<C> promise;
	protected EventExecutor eventExecutor;
	protected C value;
	protected Throwable cause;

	@Builder
	protected ProxyPromise(@NonNull EventExecutor eventExecutor, @NonNull Promise<C> promise, C value,
		Throwable cause) {
		this.eventExecutor = eventExecutor;
		this.promise = promise;
		this.value = value;
		this.cause = cause;
	}

	protected ProxyPromise<C> of(Throwable cause) {
		return new ExceptionalProxyPromise(eventExecutor, promise, value, cause);
	}

	public ProxyPromise<C> throwException(Throwable cause) {
		return this.of(cause);
	}

	public ProxyPromise<C> mapIfNot(Predicate<C> predicate, Consumer<C> function) {
		try {
			if (!hasExcetion() && predicate.negate().test(value)) {
				function.accept(value);
			}
			return this;
		} catch (Throwable cause) {
			return throwException(cause);
		}
	}

	public ProxyPromise<C> then(Predicate<C> predicate, BiConsumer<C, ? super ProxyPromise<C>> consumer) {
		try {
			if (!hasExcetion() && predicate.test(value)) {
				consumer.accept(value, this);
			}
			return this;
		} catch (Throwable cause) {
			return throwException(cause);
		}
	}

	public ProxyPromise<C> apply(Function<? super ProxyPromise<C>, ? extends ProxyPromise<C>> function) {
		try {
			if (!hasExcetion()) {
				return function.apply(this);
			}
			return this;
		} catch (Throwable cause) {
			return throwException(cause);
		}
	}

	public ProxyPromise<C> applyIfNot(Predicate<? super ProxyPromise<C>> predicate,
		Consumer<? super ProxyPromise<C>> consumer) {
		try {
			if (!hasExcetion() && predicate.negate().test(this)) {
				consumer.accept(this);
			}
			return this;
		} catch (Throwable cause) {
			return throwException(cause);
		}
	}

	public ProxyPromise<C> applyIfNot(Predicate<? super ProxyPromise<C>> predicate,
		BiConsumer<C, ? super ProxyPromise<C>> consumer) {
		try {
			if (!hasExcetion() && predicate.negate().test(this)) {
				consumer.accept(value, this);
			}
			return this;
		} catch (Throwable cause) {
			return throwException(cause);
		}
	}

	public ProxyPromise<C> throwIf(Predicate<? super ProxyPromise<C>> predicate,
		BiConsumer<Throwable, ? super ProxyPromise<C>> consumer) {
		if (predicate.test(this)) {
			consumer.accept(cause, this);
		}
		return this;
	}

	public ProxyPromise<C> whenIf(Predicate<? super ProxyPromise<C>> predicate, Consumer<? super C> consumer) {
		addListener(future -> {
			if (predicate.test(this)) {
				consumer.accept(future.getNow());
			}
		});
		return this;
	}

	public ProxyPromise<C> applyAsync(Function<? super ProxyPromise<C>, ? extends ProxyPromise<C>> function) {
		if (eventExecutor.inEventLoop()) {
			return function.apply(this);
		}
		eventExecutor.execute(() -> {
			function.apply(this);
		});
		return this;
	}

	public ProxyPromise<C> supply(Supplier<? extends ProxyPromise<C>> supplier) {
		return supplier.get();
	}

	public ProxyPromise<C> retryIf(Predicate<? super Future<C>> predicate,
		Function<? super ProxyPromise<C>, ? extends ProxyPromise<C>> retryAction,
		Consumer<? super ProxyPromise<C>> failedActions) {
		return new RetryPolicyPromise(eventExecutor, promise, value, cause)
			.retry(predicate, retryAction, failedActions);
	}

	public ProxyPromise<C> addListener(GenericFutureListener<? extends Future<C>> listener) {
		promise.addListeners(listener);
		return this;
	}

	public ProxyPromise<C> trySuccessUncompletedPromise(C value) {
		if (!isCompleted()) {
			promise.trySuccess(value);
		}
		return this;
	}

	public ProxyPromise<C> tryFailureUncompletedPromise(Throwable exception) {
		if (!isCompleted()) {
			promise.tryFailure(exception);
		}
		return this;
	}

	public ProxyPromise<C> tryCancelUncompletedPromise() {
		if (!isCompleted()) {
			promise.cancel(false);
		}
		return this;
	}

	public Future<C> acquireFuture() {
		return promise;
	}

	public boolean hasExcetion() {
		return this.cause != null;
	}

	public boolean isCompleted() {
		return promise.isDone();
	}

	public boolean isSuccess() {
		return promise.isSuccess();
	}

	public boolean isDone() {
		return promise.isDone();
	}

	public boolean isFailure() {
		return promise.cause() != null;
	}

	public boolean isCancelled() {
		return promise.isCancelled();
	}

	class ExceptionalProxyPromise extends ProxyPromise<C> {
		public ExceptionalProxyPromise(EventExecutor eventExecutor, Promise<C> promise, C value, Throwable cause) {
			super(eventExecutor, promise, value, cause);
		}

		@Override
		public boolean isCompleted() {
			return promise.isDone() && promise.cause() != null;
		}

		@Override
		public ProxyPromise<C> throwException(Throwable cause) {
			this.cause.addSuppressed(cause);
			return this;
		}

		@Override
		public boolean hasExcetion() {
			return cause != null;
		}
	}

	class RetryPolicyPromise extends ProxyPromise<C> {
		private static final int DEFAULT_MAX_ATTEMPTS = 5;
		private static final int DEFAULT_DELAY = 100;
		private AtomicInteger retryCount = new AtomicInteger();

		public RetryPolicyPromise(@NonNull EventExecutor eventExecutor, @NonNull Promise<C> promise, C value,
			Throwable cause) {
			super(eventExecutor, promise, value, cause);
		}

		public ProxyPromise<C> retry(Predicate<? super Future<C>> predicate,
			Function<? super ProxyPromise<C>, ? extends ProxyPromise<C>> retryAction,
			Consumer<? super ProxyPromise<C>> failedActions) {
			if (retryCount.incrementAndGet() > DEFAULT_MAX_ATTEMPTS) {
				failedActions.accept(this);
				return this;
			}
			retryAction.apply(this).addListener(future -> {
				if (predicate.test(future)) {
					retryWhen(() -> retry(predicate, retryAction, failedActions), DEFAULT_DELAY, TimeUnit.MILLISECONDS);
				}
			});
			return this;
		}

		private ProxyPromise<C> retryWhen(Runnable retryTask, int delay, TimeUnit timeUnit) {
			eventExecutor.schedule(retryTask, delay, timeUnit);
			return this;
		}
	}
}
