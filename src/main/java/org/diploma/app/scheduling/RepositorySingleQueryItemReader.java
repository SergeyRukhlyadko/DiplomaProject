package org.diploma.app.scheduling;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.adapter.AbstractMethodInvokingDelegator.InvocationTargetThrowableWrapper;
import org.springframework.batch.item.adapter.DynamicMethodInvocationException;
import org.springframework.data.repository.CrudRepository;
import org.springframework.util.MethodInvoker;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

public class RepositorySingleQueryItemReader<T> implements ItemStreamReader<T> {

    private CrudRepository<?, ?> repository;
    private List<?> arguments;
    private String methodName;
    private boolean queryIsNotDone = true;

    public RepositorySingleQueryItemReader(CrudRepository<?, ?> repository, String methodName, Object... arguments) {
        this.repository = repository;
        this.methodName = methodName;
        this.arguments = Arrays.asList(arguments);
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {}

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {}

    @Override
    public void close() throws ItemStreamException {
        queryIsNotDone = true;
    }

    @Override
    public T read() throws Exception {
        if (queryIsNotDone) {
            MethodInvoker invoker = createMethodInvoker(repository, methodName);

            if(arguments != null && arguments.size() > 0) {
                invoker.setArguments(arguments.toArray());
            }

            queryIsNotDone = false;

            return (T) doInvoke(invoker);
        }

        return null;
    }

    private Object doInvoke(MethodInvoker invoker) throws Exception{
        try {
            invoker.prepare();
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            throw new DynamicMethodInvocationException(e);
        }

        try {
            return invoker.invoke();
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof Exception) {
                throw (Exception) e.getCause();
            } else {
                throw new InvocationTargetThrowableWrapper(e.getCause());
            }
        } catch (IllegalAccessException e) {
            throw new DynamicMethodInvocationException(e);
        }
    }

    private MethodInvoker createMethodInvoker(CrudRepository<?, ?> targetObject, String targetMethod) {
        MethodInvoker invoker = new MethodInvoker();
        invoker.setTargetObject(targetObject);
        invoker.setTargetMethod(targetMethod);
        return invoker;
    }
}
