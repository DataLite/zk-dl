/**
 * Copyright 26.2.11 (c) DataLite, spol. s r.o. All rights reserved.
 * Web: http://www.datalite.cz    Mail: info@datalite.cz
 */
package cz.datalite.zk.components.list.filter.compilers;

/**
 * Convenient compiler for components, that support only equalls operations.
 * All other methods will throw AbstractMethodError("Operator not implemented")
 * until implemented by target class.
 */
public abstract class AbstractEqualsCompiler extends AbstractFilterCompiler {

    protected abstract Object compileOperatorEqual(String key, Object value);

    @Override
    protected Object compileOperatorEqual(String key, Object... values) {
        return compileOperatorEqual(key, values[0]);
    }

    @Override
    protected Object compileOperatorNotEqual(String key, Object... values) {
        throw new AbstractMethodError("Operator not implemented");
    }

    @Override
    protected Object compileOperatorEmpty(String key, Object... values) {
        throw new AbstractMethodError("Operator not implemented");
    }

    @Override
    protected Object compileOperatorNotEmpty(String key, Object... values) {
        throw new AbstractMethodError("Operator not implemented");
    }

    @Override
    protected Object compileOperatorLike(String key, Object... values) {
        throw new AbstractMethodError("Operator not implemented");
    }

    @Override
    protected Object compileOperatorNotLike(String key, Object... values) {
        throw new AbstractMethodError("Operator not implemented");
    }

    @Override
    protected Object compileOperatorStartWith(String key, Object... values) {
        throw new AbstractMethodError("Operator not implemented");
    }

    @Override
    protected Object compileOperatorEndWith(String key, Object... values) {
        throw new AbstractMethodError("Operator not implemented");
    }

    @Override
    protected Object compileOperatorGreaterThan(String key, Object... values) {
        throw new AbstractMethodError("Operator not implemented");
    }

    @Override
    protected Object compileOperatorGreaterEqual(String key, Object... values) {
        throw new AbstractMethodError("Operator not implemented");
    }

    @Override
    protected Object compileOperatorLesserThan(String key, Object... values) {
        throw new AbstractMethodError("Operator not implemented");
    }

    @Override
    protected Object compileOperatorLesserEqual(String key, Object... values) {
        throw new AbstractMethodError("Operator not implemented");
    }

    @Override
    protected Object compileOperatorBetween(String key, Object... values) {
        throw new AbstractMethodError("Operator not implemented");
    }
}
