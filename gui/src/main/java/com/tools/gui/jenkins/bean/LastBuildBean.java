package com.tools.gui.jenkins.bean;

import java.util.List;

public class LastBuildBean {


    /**
     * _class : hudson.model.FreeStyleBuild
     * actions : [{"_class":"hudson.model.CauseAction","causes":[{"_class":"hudson.model.Cause$UserIdCause","shortDescription":"Started by user anonymous","userId":null,"userName":"anonymous"}]},{},{"_class":"hudson.scm.SubversionTagAction"},{},{},{}]
     * artifacts : [{"displayPath":"ROOT.war","fileName":"ROOT.war","relativePath":"target/ROOT.war"},{"displayPath":"thinkwin-cr-4.2.1-SBCS.war","fileName":"thinkwin-cr-4.2.1-SBCS.war","relativePath":"target/thinkwin-cr-4.2.1-SBCS.war"}]
     * building : false
     * description : null
     * displayName : #62
     * duration : 110867
     * estimatedDuration : 119968
     * executor : null
     * fullDisplayName : 失败重试机制-thinkwin-cr #62
     * id : 62
     * keepLog : false
     * number : 62
     * queueId : 1309
     * result : SUCCESS
     * timestamp : 1527668862849
     * url : http://10.10.11.58:8080/job/%E5%A4%B1%E8%B4%A5%E9%87%8D%E8%AF%95%E6%9C%BA%E5%88%B6-thinkwin-cr/62/
     * builtOn :
     * changeSet : {"_class":"hudson.scm.SubversionChangeLogSet","items":[],"kind":"svn","revisions":[{"module":"https://sv-svn2017.chavage.com/svn/maven/source/thinkwin-cr/branches/shibaichongshi-4.2.1/thinkwin-cr","revision":95649}]}
     * culprits : []
     */

    private String _class;
    private boolean building;
    private Object description;
    private String displayName;
    private int duration;
    private int estimatedDuration;
    private Object executor;
    private String fullDisplayName;
    private String id;
    private boolean keepLog;
    private int number;
    private int queueId;
    private String result;
    private long timestamp;
    private String url;
    private String builtOn;
    private ChangeSetBean changeSet;
    private List<ActionsBean> actions;
    private List<ArtifactsBean> artifacts;
    private List<?> culprits;

    public String get_class() {
        return _class;
    }

    public void set_class(String _class) {
        this._class = _class;
    }

    public boolean isBuilding() {
        return building;
    }

    public void setBuilding(boolean building) {
        this.building = building;
    }

    public Object getDescription() {
        return description;
    }

    public void setDescription(Object description) {
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getEstimatedDuration() {
        return estimatedDuration;
    }

    public void setEstimatedDuration(int estimatedDuration) {
        this.estimatedDuration = estimatedDuration;
    }

    public Object getExecutor() {
        return executor;
    }

    public void setExecutor(Object executor) {
        this.executor = executor;
    }

    public String getFullDisplayName() {
        return fullDisplayName;
    }

    public void setFullDisplayName(String fullDisplayName) {
        this.fullDisplayName = fullDisplayName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isKeepLog() {
        return keepLog;
    }

    public void setKeepLog(boolean keepLog) {
        this.keepLog = keepLog;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getQueueId() {
        return queueId;
    }

    public void setQueueId(int queueId) {
        this.queueId = queueId;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBuiltOn() {
        return builtOn;
    }

    public void setBuiltOn(String builtOn) {
        this.builtOn = builtOn;
    }

    public ChangeSetBean getChangeSet() {
        return changeSet;
    }

    public void setChangeSet(ChangeSetBean changeSet) {
        this.changeSet = changeSet;
    }

    public List<ActionsBean> getActions() {
        return actions;
    }

    public void setActions(List<ActionsBean> actions) {
        this.actions = actions;
    }

    public List<ArtifactsBean> getArtifacts() {
        return artifacts;
    }

    public void setArtifacts(List<ArtifactsBean> artifacts) {
        this.artifacts = artifacts;
    }

    public List<?> getCulprits() {
        return culprits;
    }

    public void setCulprits(List<?> culprits) {
        this.culprits = culprits;
    }

    public static class ChangeSetBean {
        /**
         * _class : hudson.scm.SubversionChangeLogSet
         * items : []
         * kind : svn
         * revisions : [{"module":"https://sv-svn2017.chavage.com/svn/maven/source/thinkwin-cr/branches/shibaichongshi-4.2.1/thinkwin-cr","revision":95649}]
         */

        private String _class;
        private String kind;
        private List<?> items;
        private List<RevisionsBean> revisions;

        public String get_class() {
            return _class;
        }

        public void set_class(String _class) {
            this._class = _class;
        }

        public String getKind() {
            return kind;
        }

        public void setKind(String kind) {
            this.kind = kind;
        }

        public List<?> getItems() {
            return items;
        }

        public void setItems(List<?> items) {
            this.items = items;
        }

        public List<RevisionsBean> getRevisions() {
            return revisions;
        }

        public void setRevisions(List<RevisionsBean> revisions) {
            this.revisions = revisions;
        }

        public static class RevisionsBean {
            /**
             * module : https://sv-svn2017.chavage.com/svn/maven/source/thinkwin-cr/branches/shibaichongshi-4.2.1/thinkwin-cr
             * revision : 95649
             */

            private String module;
            private int revision;

            public String getModule() {
                return module;
            }

            public void setModule(String module) {
                this.module = module;
            }

            public int getRevision() {
                return revision;
            }

            public void setRevision(int revision) {
                this.revision = revision;
            }
        }
    }

    public static class ActionsBean {
        /**
         * _class : hudson.model.CauseAction
         * causes : [{"_class":"hudson.model.Cause$UserIdCause","shortDescription":"Started by user anonymous","userId":null,"userName":"anonymous"}]
         */

        private String _class;
        private List<CausesBean> causes;

        public String get_class() {
            return _class;
        }

        public void set_class(String _class) {
            this._class = _class;
        }

        public List<CausesBean> getCauses() {
            return causes;
        }

        public void setCauses(List<CausesBean> causes) {
            this.causes = causes;
        }

        public static class CausesBean {
            /**
             * _class : hudson.model.Cause$UserIdCause
             * shortDescription : Started by user anonymous
             * userId : null
             * userName : anonymous
             */

            private String _class;
            private String shortDescription;
            private Object userId;
            private String userName;

            public String get_class() {
                return _class;
            }

            public void set_class(String _class) {
                this._class = _class;
            }

            public String getShortDescription() {
                return shortDescription;
            }

            public void setShortDescription(String shortDescription) {
                this.shortDescription = shortDescription;
            }

            public Object getUserId() {
                return userId;
            }

            public void setUserId(Object userId) {
                this.userId = userId;
            }

            public String getUserName() {
                return userName;
            }

            public void setUserName(String userName) {
                this.userName = userName;
            }
        }
    }

    public static class ArtifactsBean {
        /**
         * displayPath : ROOT.war
         * fileName : ROOT.war
         * relativePath : target/ROOT.war
         */

        private String displayPath;
        private String fileName;
        private String relativePath;

        public String getDisplayPath() {
            return displayPath;
        }

        public void setDisplayPath(String displayPath) {
            this.displayPath = displayPath;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getRelativePath() {
            return relativePath;
        }

        public void setRelativePath(String relativePath) {
            this.relativePath = relativePath;
        }
    }
}
