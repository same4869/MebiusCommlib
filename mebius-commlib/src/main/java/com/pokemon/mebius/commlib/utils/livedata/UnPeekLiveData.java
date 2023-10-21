package com.pokemon.mebius.commlib.utils.livedata;

/**
 * 防止数据倒灌的livedata 参考https://juejin.cn/post/7058442981501567007
 * @param <T>
 */
public class UnPeekLiveData<T> extends ProtectedUnPeekLiveData<T> {

    @Override
    public void setValue(T value) {
        super.setValue(value);
    }

    @Override
    public void postValue(T value) {
        super.postValue(value);
    }

    public static class Builder<T> {

        /**
         * 是否允许传入 null value
         */
        private boolean isAllowNullValue;

        public Builder<T> setAllowNullValue(boolean allowNullValue) {
            this.isAllowNullValue = allowNullValue;
            return this;
        }

        public UnPeekLiveData<T> create() {
            UnPeekLiveData<T> liveData = new UnPeekLiveData<>();
            liveData.isAllowNullValue = this.isAllowNullValue;
            return liveData;
        }
    }
}
