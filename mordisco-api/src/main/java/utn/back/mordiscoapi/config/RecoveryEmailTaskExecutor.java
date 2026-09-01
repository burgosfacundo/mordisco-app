package utn.back.mordiscoapi.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.TaskRejectedException;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Slf4j
@RequiredArgsConstructor
public class RecoveryEmailTaskExecutor implements AsyncTaskExecutor, DisposableBean {
    private final AsyncTaskExecutor delegate;

    @Override
    public void execute(Runnable task) {
        submit(task);
    }

    @Override
    public Future<?> submit(Runnable task) {
        try {
            return delegate.submit(task);
        } catch (TaskRejectedException ignored) {
            log.error("Password recovery email dispatch unavailable");
            return CompletableFuture.completedFuture(null);
        }
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        try {
            return delegate.submit(task);
        } catch (TaskRejectedException ignored) {
            log.error("Password recovery email dispatch unavailable");
            return CompletableFuture.completedFuture(null);
        }
    }

    @Override
    public void destroy() throws Exception {
        if (delegate instanceof DisposableBean disposableBean) {
            disposableBean.destroy();
        }
    }
}
