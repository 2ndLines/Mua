package com.baoshi.mua.request;

import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.baoshi.mua.model.IModel;

import java.util.Collection;

/**
 * Created by ThinkPad on 2014/11/5.
 */
public class Parameter {

    public enum Action{
        Query,Delete,SaveOrUpdate,Upload
    }

    IModel body;
    Action action;
    int retryCount;
    AVQuery queryInstance;

    private Parameter(Builder builder){
        this.body = builder.body;
        this.action = builder.action;
        this.retryCount = builder.retryCount;
        this.queryInstance = builder.queryInstance;
    }

    public IModel getBody() {
        return body;
    }

    public Action getAction() {
        return action;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public  AVQuery getQueryInstance(){
        return queryInstance;
    }

    public static class Builder{
        private IModel body;
        private Action action;
        private int retryCount;
        private AVQuery queryInstance;

        public Builder(String className){
            withClassName(className);
        }

        public Builder clear(){
            this.body = null;
            this.action = null;
            this.retryCount = 0;
            queryInstance = null;

            return this;
        }

        public Builder setQueryInstance(AVQuery queryInstance){
            this.queryInstance = queryInstance;
            return this;
        }

        public Builder setAction(Action action){
            this.action = action;
            return this;
        }

        public Builder setBody(IModel body){
            this.body = body;
            return this;
        }

        public Builder setRetryCount(int retryCount){
            this.retryCount = retryCount;
            return this;
        }

        public Builder withObjectId(String objectId) {
            if(body != null){
                body.setId(objectId);
            }
            return this;
        }

        private Builder withClassName(String className){
            if(queryInstance == null) queryInstance = new AVQuery<AVObject>();
            queryInstance.setClassName(className);
            return this;
        }
        public Builder withLimit(int limit){
            if(queryInstance == null) queryInstance = new AVQuery<AVObject>();
            queryInstance.setLimit(limit);
            return this;
        }

        public Builder withSelectKeys(Collection<String> columns){
            if(queryInstance == null) queryInstance = new AVQuery<AVObject>();
            queryInstance.selectKeys(columns);
            return this;
        }

        public Builder withInclude(String column){
            if(queryInstance == null) queryInstance = new AVQuery<AVObject>();
            queryInstance.include(column);
            return this;
        }
        public Parameter build(){
            return new Parameter(this);
        }
    }
}
