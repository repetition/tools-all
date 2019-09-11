package com.tools.gui.jenkins.bean;

import java.util.List;

public class ProjectDetailBean {


    /**
     * _class : hudson.model.FreeStyleProject
     * actions : [{},{},{"_class":"com.cloudbees.plugins.credentials.ViewCredentialsAction"}]
     * description :
     * displayName : 失败重试机制-thinkwin-cr
     * displayNameOrNull : null
     * fullDisplayName : 失败重试机制-thinkwin-cr
     * fullName : 失败重试机制-thinkwin-cr
     * name : 失败重试机制-thinkwin-cr
     * url : http://10.10.11.58:8080/job/%E5%A4%B1%E8%B4%A5%E9%87%8D%E8%AF%95%E6%9C%BA%E5%88%B6-thinkwin-cr/
     * buildable : true
     * builds : [{"_class":"hudson.model.FreeStyleBuild","number":55,"url":"http://10.10.11.58:8080/job/%E5%A4%B1%E8%B4%A5%E9%87%8D%E8%AF%95%E6%9C%BA%E5%88%B6-thinkwin-cr/55/"},{"_class":"hudson.model.FreeStyleBuild","number":54,"url":"http://10.10.11.58:8080/job/%E5%A4%B1%E8%B4%A5%E9%87%8D%E8%AF%95%E6%9C%BA%E5%88%B6-thinkwin-cr/54/"},{"_class":"hudson.model.FreeStyleBuild","number":53,"url":"http://10.10.11.58:8080/job/%E5%A4%B1%E8%B4%A5%E9%87%8D%E8%AF%95%E6%9C%BA%E5%88%B6-thinkwin-cr/53/"},{"_class":"hudson.model.FreeStyleBuild","number":52,"url":"http://10.10.11.58:8080/job/%E5%A4%B1%E8%B4%A5%E9%87%8D%E8%AF%95%E6%9C%BA%E5%88%B6-thinkwin-cr/52/"},{"_class":"hudson.model.FreeStyleBuild","number":51,"url":"http://10.10.11.58:8080/job/%E5%A4%B1%E8%B4%A5%E9%87%8D%E8%AF%95%E6%9C%BA%E5%88%B6-thinkwin-cr/51/"},{"_class":"hudson.model.FreeStyleBuild","number":50,"url":"http://10.10.11.58:8080/job/%E5%A4%B1%E8%B4%A5%E9%87%8D%E8%AF%95%E6%9C%BA%E5%88%B6-thinkwin-cr/50/"},{"_class":"hudson.model.FreeStyleBuild","number":49,"url":"http://10.10.11.58:8080/job/%E5%A4%B1%E8%B4%A5%E9%87%8D%E8%AF%95%E6%9C%BA%E5%88%B6-thinkwin-cr/49/"},{"_class":"hudson.model.FreeStyleBuild","number":48,"url":"http://10.10.11.58:8080/job/%E5%A4%B1%E8%B4%A5%E9%87%8D%E8%AF%95%E6%9C%BA%E5%88%B6-thinkwin-cr/48/"},{"_class":"hudson.model.FreeStyleBuild","number":47,"url":"http://10.10.11.58:8080/job/%E5%A4%B1%E8%B4%A5%E9%87%8D%E8%AF%95%E6%9C%BA%E5%88%B6-thinkwin-cr/47/"},{"_class":"hudson.model.FreeStyleBuild","number":46,"url":"http://10.10.11.58:8080/job/%E5%A4%B1%E8%B4%A5%E9%87%8D%E8%AF%95%E6%9C%BA%E5%88%B6-thinkwin-cr/46/"}]
     * color : blue
     * firstBuild : {"_class":"hudson.model.FreeStyleBuild","number":46,"url":"http://10.10.11.58:8080/job/%E5%A4%B1%E8%B4%A5%E9%87%8D%E8%AF%95%E6%9C%BA%E5%88%B6-thinkwin-cr/46/"}
     * healthReport : [{"description":"Build stability: No recent builds failed.","iconClassName":"icon-health-80plus","iconUrl":"health-80plus.png","score":100}]
     * inQueue : false
     * keepDependencies : false
     * lastBuild : {"_class":"hudson.model.FreeStyleBuild","number":55,"url":"http://10.10.11.58:8080/job/%E5%A4%B1%E8%B4%A5%E9%87%8D%E8%AF%95%E6%9C%BA%E5%88%B6-thinkwin-cr/55/"}
     * lastCompletedBuild : {"_class":"hudson.model.FreeStyleBuild","number":55,"url":"http://10.10.11.58:8080/job/%E5%A4%B1%E8%B4%A5%E9%87%8D%E8%AF%95%E6%9C%BA%E5%88%B6-thinkwin-cr/55/"}
     * lastFailedBuild : null
     * lastStableBuild : {"_class":"hudson.model.FreeStyleBuild","number":55,"url":"http://10.10.11.58:8080/job/%E5%A4%B1%E8%B4%A5%E9%87%8D%E8%AF%95%E6%9C%BA%E5%88%B6-thinkwin-cr/55/"}
     * lastSuccessfulBuild : {"_class":"hudson.model.FreeStyleBuild","number":55,"url":"http://10.10.11.58:8080/job/%E5%A4%B1%E8%B4%A5%E9%87%8D%E8%AF%95%E6%9C%BA%E5%88%B6-thinkwin-cr/55/"}
     * lastUnstableBuild : null
     * lastUnsuccessfulBuild : null
     * nextBuildNumber : 56
     * property : [{"_class":"jenkins.model.BuildDiscarderProperty"}]
     * queueItem : null
     * concurrentBuild : false
     * downstreamProjects : []
     * scm : {"_class":"hudson.scm.SubversionSCM"}
     * upstreamProjects : []
     */

    private String _class;
    private String description;
    private String displayName;
    private Object displayNameOrNull;
    private String fullDisplayName;
    private String fullName;
    private String name;
    private String url;
    private boolean buildable;
    private String color;
    private FirstBuildBean firstBuild;
    private boolean inQueue;
    private boolean keepDependencies;
    private LastBuildBean lastBuild;
    private LastCompletedBuildBean lastCompletedBuild;
    private Object lastFailedBuild;
    private LastStableBuildBean lastStableBuild;
    private LastSuccessfulBuildBean lastSuccessfulBuild;
    private Object lastUnstableBuild;
    private Object lastUnsuccessfulBuild;
    private int nextBuildNumber;
    private Object queueItem;
    private boolean concurrentBuild;
    private ScmBean scm;
    private List<ActionsBean> actions;
    private List<BuildsBean> builds;
    private List<HealthReportBean> healthReport;
    private List<PropertyBean> property;
    private List<?> downstreamProjects;
    private List<?> upstreamProjects;

    public String get_class() {
        return _class;
    }

    public void set_class(String _class) {
        this._class = _class;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Object getDisplayNameOrNull() {
        return displayNameOrNull;
    }

    public void setDisplayNameOrNull(Object displayNameOrNull) {
        this.displayNameOrNull = displayNameOrNull;
    }

    public String getFullDisplayName() {
        return fullDisplayName;
    }

    public void setFullDisplayName(String fullDisplayName) {
        this.fullDisplayName = fullDisplayName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isBuildable() {
        return buildable;
    }

    public void setBuildable(boolean buildable) {
        this.buildable = buildable;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public FirstBuildBean getFirstBuild() {
        return firstBuild;
    }

    public void setFirstBuild(FirstBuildBean firstBuild) {
        this.firstBuild = firstBuild;
    }

    public boolean isInQueue() {
        return inQueue;
    }

    public void setInQueue(boolean inQueue) {
        this.inQueue = inQueue;
    }

    public boolean isKeepDependencies() {
        return keepDependencies;
    }

    public void setKeepDependencies(boolean keepDependencies) {
        this.keepDependencies = keepDependencies;
    }

    public LastBuildBean getLastBuild() {
        return lastBuild;
    }

    public void setLastBuild(LastBuildBean lastBuild) {
        this.lastBuild = lastBuild;
    }

    public LastCompletedBuildBean getLastCompletedBuild() {
        return lastCompletedBuild;
    }

    public void setLastCompletedBuild(LastCompletedBuildBean lastCompletedBuild) {
        this.lastCompletedBuild = lastCompletedBuild;
    }

    public Object getLastFailedBuild() {
        return lastFailedBuild;
    }

    public void setLastFailedBuild(Object lastFailedBuild) {
        this.lastFailedBuild = lastFailedBuild;
    }

    public LastStableBuildBean getLastStableBuild() {
        return lastStableBuild;
    }

    public void setLastStableBuild(LastStableBuildBean lastStableBuild) {
        this.lastStableBuild = lastStableBuild;
    }

    public LastSuccessfulBuildBean getLastSuccessfulBuild() {
        return lastSuccessfulBuild;
    }

    public void setLastSuccessfulBuild(LastSuccessfulBuildBean lastSuccessfulBuild) {
        this.lastSuccessfulBuild = lastSuccessfulBuild;
    }

    public Object getLastUnstableBuild() {
        return lastUnstableBuild;
    }

    public void setLastUnstableBuild(Object lastUnstableBuild) {
        this.lastUnstableBuild = lastUnstableBuild;
    }

    public Object getLastUnsuccessfulBuild() {
        return lastUnsuccessfulBuild;
    }

    public void setLastUnsuccessfulBuild(Object lastUnsuccessfulBuild) {
        this.lastUnsuccessfulBuild = lastUnsuccessfulBuild;
    }

    public int getNextBuildNumber() {
        return nextBuildNumber;
    }

    public void setNextBuildNumber(int nextBuildNumber) {
        this.nextBuildNumber = nextBuildNumber;
    }

    public Object getQueueItem() {
        return queueItem;
    }

    public void setQueueItem(Object queueItem) {
        this.queueItem = queueItem;
    }

    public boolean isConcurrentBuild() {
        return concurrentBuild;
    }

    public void setConcurrentBuild(boolean concurrentBuild) {
        this.concurrentBuild = concurrentBuild;
    }

    public ScmBean getScm() {
        return scm;
    }

    public void setScm(ScmBean scm) {
        this.scm = scm;
    }

    public List<ActionsBean> getActions() {
        return actions;
    }

    public void setActions(List<ActionsBean> actions) {
        this.actions = actions;
    }

    public List<BuildsBean> getBuilds() {
        return builds;
    }

    public void setBuilds(List<BuildsBean> builds) {
        this.builds = builds;
    }

    public List<HealthReportBean> getHealthReport() {
        return healthReport;
    }

    public void setHealthReport(List<HealthReportBean> healthReport) {
        this.healthReport = healthReport;
    }

    public List<PropertyBean> getProperty() {
        return property;
    }

    public void setProperty(List<PropertyBean> property) {
        this.property = property;
    }

    public List<?> getDownstreamProjects() {
        return downstreamProjects;
    }

    public void setDownstreamProjects(List<?> downstreamProjects) {
        this.downstreamProjects = downstreamProjects;
    }

    public List<?> getUpstreamProjects() {
        return upstreamProjects;
    }

    public void setUpstreamProjects(List<?> upstreamProjects) {
        this.upstreamProjects = upstreamProjects;
    }

    public static class FirstBuildBean {
        /**
         * _class : hudson.model.FreeStyleBuild
         * number : 46
         * url : http://10.10.11.58:8080/job/%E5%A4%B1%E8%B4%A5%E9%87%8D%E8%AF%95%E6%9C%BA%E5%88%B6-thinkwin-cr/46/
         */

        private String _class;
        private int number;
        private String url;

        public String get_class() {
            return _class;
        }

        public void set_class(String _class) {
            this._class = _class;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public static class LastBuildBean {
        /**
         * _class : hudson.model.FreeStyleBuild
         * number : 55
         * url : http://10.10.11.58:8080/job/%E5%A4%B1%E8%B4%A5%E9%87%8D%E8%AF%95%E6%9C%BA%E5%88%B6-thinkwin-cr/55/
         */

        private String _class;
        private int number;
        private String url;

        public String get_class() {
            return _class;
        }

        public void set_class(String _class) {
            this._class = _class;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public static class LastCompletedBuildBean {
        /**
         * _class : hudson.model.FreeStyleBuild
         * number : 55
         * url : http://10.10.11.58:8080/job/%E5%A4%B1%E8%B4%A5%E9%87%8D%E8%AF%95%E6%9C%BA%E5%88%B6-thinkwin-cr/55/
         */

        private String _class;
        private int number;
        private String url;

        public String get_class() {
            return _class;
        }

        public void set_class(String _class) {
            this._class = _class;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public static class LastStableBuildBean {
        /**
         * _class : hudson.model.FreeStyleBuild
         * number : 55
         * url : http://10.10.11.58:8080/job/%E5%A4%B1%E8%B4%A5%E9%87%8D%E8%AF%95%E6%9C%BA%E5%88%B6-thinkwin-cr/55/
         */

        private String _class;
        private int number;
        private String url;

        public String get_class() {
            return _class;
        }

        public void set_class(String _class) {
            this._class = _class;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public static class LastSuccessfulBuildBean {
        /**
         * _class : hudson.model.FreeStyleBuild
         * number : 55
         * url : http://10.10.11.58:8080/job/%E5%A4%B1%E8%B4%A5%E9%87%8D%E8%AF%95%E6%9C%BA%E5%88%B6-thinkwin-cr/55/
         */

        private String _class;
        private int number;
        private String url;

        public String get_class() {
            return _class;
        }

        public void set_class(String _class) {
            this._class = _class;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public static class ScmBean {
        /**
         * _class : hudson.scm.SubversionSCM
         */

        private String _class;

        public String get_class() {
            return _class;
        }

        public void set_class(String _class) {
            this._class = _class;
        }
    }

    public static class ActionsBean {
        /**
         * _class : com.cloudbees.plugins.credentials.ViewCredentialsAction
         */

        private String _class;

        public String get_class() {
            return _class;
        }

        public void set_class(String _class) {
            this._class = _class;
        }
    }

    public static class BuildsBean {
        /**
         * _class : hudson.model.FreeStyleBuild
         * number : 55
         * url : http://10.10.11.58:8080/job/%E5%A4%B1%E8%B4%A5%E9%87%8D%E8%AF%95%E6%9C%BA%E5%88%B6-thinkwin-cr/55/
         */

        private String _class;
        private int number;
        private String url;

        public String get_class() {
            return _class;
        }

        public void set_class(String _class) {
            this._class = _class;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public static class HealthReportBean {
        /**
         * description : Build stability: No recent builds failed.
         * iconClassName : icon-health-80plus
         * iconUrl : health-80plus.png
         * score : 100
         */

        private String description;
        private String iconClassName;
        private String iconUrl;
        private int score;

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getIconClassName() {
            return iconClassName;
        }

        public void setIconClassName(String iconClassName) {
            this.iconClassName = iconClassName;
        }

        public String getIconUrl() {
            return iconUrl;
        }

        public void setIconUrl(String iconUrl) {
            this.iconUrl = iconUrl;
        }

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }
    }

    public static class PropertyBean {
        /**
         * _class : jenkins.model.BuildDiscarderProperty
         */

        private String _class;

        public String get_class() {
            return _class;
        }

        public void set_class(String _class) {
            this._class = _class;
        }
    }
}
