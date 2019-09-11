package com.tools.service.model;

import com.tools.service.constant.TaskEnum;

public class DeployState{

        private TaskEnum taskEnum;
        private String e;
        private String info;
        private boolean state;

        public TaskEnum getTaskEnum() {
            return taskEnum;
        }

        public DeployState setTaskEnum(TaskEnum taskEnum) {
            this.taskEnum = taskEnum;
            return this;
        }

        public String getE() {
            return e;
        }

        public DeployState setE(String e) {
            this.e = e;
            return this;
        }

        public String getInfo() {
            return info;
        }

        public DeployState setInfo(String info) {
            this.info = info;
            return this;
        }

        public boolean state() {
            return state;
        }

        public DeployState setState(boolean state) {
            this.state = state;
            return this;
        }
    }
