package channelpool;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;

class ProxyPromiseTest {
	private static final EventLoopGroup dummyGroup = new DefaultEventLoopGroup(2);

	@Test
	public void testCatchException() {
		String expectedExceptionMessage = "A test exception occurred.";
		EventExecutor eventExecutor = dummyGroup.next();
		ProxyPromise<String> proxyPromise = new ProxyPromise(eventExecutor, eventExecutor.newPromise(), "Hello World",
			null);

		proxyPromise
			.throwException(new RuntimeException(expectedExceptionMessage))
			.applyIfNot(promise -> promise.hasExcetion(), promise -> promise.tryCancel())
			.throwIf(promise -> promise.hasExcetion(), (cause, promise) -> promise.tryFailure(cause));

		Future<String> future = proxyPromise.acquireFuture();
		future.awaitUninterruptibly();

		Assertions.assertFalse(future.isCancelled());
		Assertions.assertTrue(future.cause() != null);
		Assertions.assertEquals(expectedExceptionMessage, future.cause().getMessage());
	}

	@Test
	public void testCancelPromise() {
		EventExecutor eventExecutor = dummyGroup.next();
		ProxyPromise<String> proxyPromise = new ProxyPromise(eventExecutor, eventExecutor.newPromise(), "Hello World",
			null);

		proxyPromise
			.applyIfNot(promise -> promise.isCompleted(), promise -> promise.tryCancel())
			.throwException(new RuntimeException())
			.throwIf(promise -> promise.hasExcetion(), (cause, promise) -> promise.tryFailure(cause));

		Future<String> future = proxyPromise.acquireFuture();
		future.awaitUninterruptibly();

		String expectResult = "CancellationException";
		String actualResult = future.cause().toString();

		Assertions.assertTrue(future.isCancelled());
		Assertions.assertTrue(actualResult.contains(expectResult));
	}

	@Test
	public void testSuccessPromise() {
		String expectedValue = "Hello World";
		EventExecutor eventExecutor = dummyGroup.next();
		ProxyPromise<String> proxyPromise = new ProxyPromise(eventExecutor, eventExecutor.newPromise(), expectedValue,
			null);

		proxyPromise
			.then(value -> value.equals(expectedValue), (value, promise) -> promise.trySuccess(value))
			.applyIfNot(promise -> promise.isCompleted(), promise -> promise.tryCancel());

		Future<String> future = proxyPromise.acquireFuture();
		future.awaitUninterruptibly();

		Assertions.assertTrue(future.isSuccess());
		Assertions.assertEquals(expectedValue, future.getNow());
	}

	@Test
	public void testInEventLoop() {
		EventExecutor eventExecutor = dummyGroup.next();
		ProxyPromise<String> proxyPromise = new ProxyPromise(eventExecutor, eventExecutor.newPromise(), null, null);
		proxyPromise.applyAsync(promise -> {
			Assertions.assertTrue(eventExecutor.inEventLoop(Thread.currentThread()));
			return promise;
		}).apply(promise -> promise.tryCancel());

		proxyPromise.acquireFuture().awaitUninterruptibly();
	}
}