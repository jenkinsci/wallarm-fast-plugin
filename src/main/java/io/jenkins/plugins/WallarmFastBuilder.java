package io.jenkins.plugins;

import java.util.*;
import java.io.*;
import hudson.AbortException;
import hudson.Launcher;
import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.util.FormValidation;
import hudson.util.Secret;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.Builder;
import hudson.tasks.BuildStepDescriptor;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import javax.servlet.ServletException;
import java.io.IOException;
import jenkins.tasks.SimpleBuildStep;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundSetter;

import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.StaplerRequest;


public class WallarmFastBuilder extends Builder implements SimpleBuildStep {

    private Secret  wallarmApiToken;
    private String  appHost;
    private String  appPort;
    private String  fastPort;
    private String  fastName;
    private int     policyId;
    private int     testRecordId;
    private String  wallarmApiHost;
    private String  testRunName;
    private String  testRunDesc;
    private boolean record;
    private boolean stopOnFirstFail;
    private boolean failBuild;
    private boolean withoutSudo;
    private String  localDockerNetwork;
    private String  localDockerIp;
    private String  wallarmVersion;
    private String  fileExtensionsToExclude;
    private int     inactivityTimeout;
    private int     testRunRps;

    @DataBoundConstructor
    public WallarmFastBuilder(
        Secret  wallarmApiToken,
        String  appHost,
        String  appPort,
        String  fastPort,
        String  fastName,
        int     policyId,
        int     testRecordId,
        String  wallarmApiHost,
        String  testRunName,
        String  testRunDesc,
        boolean record,
        boolean stopOnFirstFail,
        boolean failBuild,
        boolean withoutSudo,
        String  localDockerNetwork,
        String  localDockerIp,
        String  wallarmVersion,
        String  fileExtensionsToExclude,
        int     inactivityTimeout,
        int     testRunRps) {

        super();
        this.wallarmApiToken = wallarmApiToken;
        this.appHost = appHost;
        this.appPort = appPort;
        this.fastPort = fastPort;
        this.policyId = policyId;
        this.testRecordId = testRecordId;
        this.wallarmApiHost = wallarmApiHost;
        this.testRunName = testRunName;
        this.testRunDesc = testRunDesc;
        this.record = record;
        this.stopOnFirstFail = stopOnFirstFail;
        this.failBuild = failBuild;
        this.withoutSudo = withoutSudo;
        this.localDockerNetwork = localDockerNetwork;
        this.localDockerIp = localDockerIp;
        this.wallarmVersion = not_empty(wallarmVersion) ? wallarmVersion : "latest";
        this.fileExtensionsToExclude = fileExtensionsToExclude;
        this.inactivityTimeout = inactivityTimeout;
        this.testRunRps = testRunRps;

        if (not_empty(fastName) ) {
            this.fastName = fastName;
        } else {
            this.fastName = record ? "wallarm_fast_recorder" : "wallarm_fast_tester";
        }
    }

    public String getWallarmApiToken() {
        return wallarmApiToken.getPlainText();
    }

    public String getAppHost() {
        return appHost;
    }

    public String getAppPort() {
        return appPort;
    }

    public String getFastPort() {
        return fastPort;
    }

    public String getFastName() {
        return fastName;
    }

    public int getPolicyId() {
        return policyId;
    }

    public int getTestRecordId() {
        return testRecordId;
    }

    public String getWallarmApiHost() {
        return wallarmApiHost;
    }

    public String getTestRunName() {
        return testRunName;
    }

    public String getTestRunDesc() {
        return testRunDesc;
    }

    public boolean getRecord() {
        return record;
    }

    public boolean getStopOnFirstFail() {
        return stopOnFirstFail;
    }

    public boolean getFailBuild() {
        return failBuild;
    }

    public boolean getWithoutSudo() {
        return withoutSudo;
    }

    public String getLocalDockerNetwork() {
        return localDockerNetwork;
    }

    public String getLocalDockerIp() {
        return localDockerIp;
    }

    public String getWallarmVersion() {
        return wallarmVersion;
    }

    public String getFileExtensionsToExclude() {
        return fileExtensionsToExclude;
    }

    public int getInactivityTimeout() {
        return inactivityTimeout;
    }

    public int getTestRunRps() {
        return testRunRps;
    }

    @DataBoundSetter

    public void setWallarmApiToken (String wallarmApiToken) {
        this.wallarmApiToken = Secret.fromString(wallarmApiToken);
    }
    public void setAppHost (String appHost) {
        this.appHost = appHost;
    }
    public void setAppPort (String appPort) {
        this.appPort = appPort;
    }
    public void setFastPort (String fastPort) {
        this.fastPort = fastPort;
    }
    public void setFastName (String fastName) {
        this.fastName = fastName;
    }
    public void setPolicyId (int policyId) {
        this.policyId = policyId;
    }
    public void setTestRecordId (int testRecordId) {
        this.testRecordId = testRecordId;
    }
    public void setWallarmApiHost (String wallarmApiHost) {
        this.wallarmApiHost = wallarmApiHost;
    }
    public void setTestRunName (String testRunName) {
        this.testRunName = testRunName;
    }
    public void setTestRunDesc (String testRunDesc) {
        this.testRunDesc = testRunDesc;
    }
    public void setRecord (boolean record) {
        this.record = record;
    }
    public void setStopOnFirstFail (boolean stopOnFirstFail) {
        this.stopOnFirstFail = stopOnFirstFail;
    }
    public void setFailBuild (boolean failBuild) {
        this.failBuild = failBuild;
    }
    public void setWithoutSudo (boolean withoutSudo) {
        this.withoutSudo = withoutSudo;
    }
    public void setLocalDockerNetwork (String localDockerNetwork) {
        this.localDockerNetwork = localDockerNetwork;
    }
    public void setLocalDockerIp (String localDockerIp) {
        this.localDockerIp = localDockerIp;
    }
    public void setWallarmVersion (String wallarmVersion) {
        this.wallarmVersion = wallarmVersion;
    }
    public void setFileExtensionsToExclude (String fileExtensionsToExclude) {
        this.fileExtensionsToExclude = fileExtensionsToExclude;
    }
    public void setInactivityTimeout (int inactivityTimeout) {
        this.inactivityTimeout = inactivityTimeout;
    }
    public void setTestRunRps (int testRunRps) {
        this.testRunRps = testRunRps;
    }

    @Override
    public void perform(
        Run<?, ?> run,
        FilePath workspace,
        Launcher launcher,
        TaskListener listener
        ) throws InterruptedException, IOException {

        List<String> cmd = new ArrayList<String>();

        add_required_params(cmd);
        add_optional_params(cmd);

        if (record) {
            add_record_params(cmd);
            cmd.add("wallarm/fast:" + wallarmVersion ); // this must be the last parameter!
            record_baselines(cmd, run, launcher, listener);
        } else {
            add_testing_params(cmd);
            cmd.add("wallarm/fast:" + wallarmVersion ); // this must be the last parameter!
            run_tests(cmd, run, launcher, listener);
        }
    }

    // some overloaded not_empty checks to mimic the original Ruby code
    public boolean not_empty(String param) {
        return (param != null && !param.isEmpty());
    }

    public boolean not_empty(int param) {
        return param != 0; //while not always correct in our context, it's close enough to use
    }

    public boolean not_empty(boolean param) {
        return true;
    }

    public void add_required_params(List<String> cmd) {
        cmd.add("docker run --rm");
        cmd.add("-e WALLARM_API_TOKEN=$WALLARM_API_TOKEN");
        cmd.add("-e WALLARM_API_HOST=" + wallarmApiHost );
        if (not_empty(appHost)) {cmd.add("-e TEST_RUN_URI=http://" + appHost + ":" + appPort);}
    }

    public void add_record_params(List<String> cmd) {
        cmd.add("-d");
        cmd.add("-e CI_MODE=recording");
        cmd.add("-p " + fastPort + ":8080");
        cmd.add("-e INACTIVITY_TIMEOUT=" + inactivityTimeout );
    }

    public void add_testing_params(List<String> cmd) {
        cmd.add("-e CI_MODE=testing");
        if (not_empty(testRecordId))            {cmd.add("-e TEST_RECORD_ID=" + testRecordId);}
        if (not_empty(policyId))                {cmd.add("-e TEST_RUN_POLICY_ID=" + policyId);}
        if (not_empty(testRunRps))              {cmd.add("-e TEST_RUN_RPS=" + testRunRps);}
        if (not_empty(testRunName))             {cmd.add("-e TEST_RUN_NAME=" + testRunName.replace(" ", "_"));}
        if (not_empty(testRunDesc))             {cmd.add("-e TEST_RUN_DESC=" + testRunDesc.replace(" ", "_"));}
        if (not_empty(stopOnFirstFail))         {cmd.add("-e TEST_RUN_STOP_ON_FIRST_FAIL=" + stopOnFirstFail);}
        if (not_empty(fileExtensionsToExclude)) {cmd.add("-e FILE_EXTENSIONS_TO_EXCLUDE=" + fileExtensionsToExclude);}
    }

    public void add_optional_params(List<String> cmd) {
        cmd.add("--name " + fastName);
        if (not_empty(localDockerNetwork))      {cmd.add("--net " + localDockerNetwork);}
        if (not_empty(localDockerIp))           {cmd.add("--ip " + localDockerIp);}
    }

    // this one is used when we need to parse the output of the command we're launching
    public String shell_command(
            Launcher launcher,
            List<String> cmd
            ) throws java.io.IOException, java.lang.InterruptedException {

        if (!withoutSudo && cmd.get(0) != "sudo") {
            cmd.add(0, "sudo");
        }
        EnvVars env = new EnvVars();
        env.put("WALLARM_API_TOKEN", wallarmApiToken.getPlainText());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        launcher.launch()
            .cmds(String.join(" ", cmd).split(" "))
            .envs(env)
            .stdout(out)
            .join();

        return out.toString("UTF-8").trim();
     }

    // this one is used when we do not really care about the text result - just the launching itself
    public int execute_cmd(
            Launcher launcher,
            TaskListener listener,
            List<String> cmd) {

        if (!withoutSudo && cmd.get(0) != "sudo") {
            cmd.add(0, "sudo");
        }

        try {
            EnvVars env = new EnvVars();
            env.put("WALLARM_API_TOKEN", wallarmApiToken.getPlainText());

            int result = launcher.launch()
                .cmds(String.join(" ", cmd).split(" "))
                .envs(env)
                .stderr(listener.getLogger())
                .stdout(listener.getLogger())
                .join();
            return result;
        }
        catch (java.io.IOException | java.lang.InterruptedException error)
        {
            return -1;
        }
    }

    public void record_baselines(
            List<String> cmd,
            Run<?, ?> build,
            Launcher launcher,
            TaskListener listener) throws IOException, InterruptedException {
        // launcher will give a proc starter Object
        // typically, you should use start() to launch and forget
        // however, you may also use join() to launch and wait (useful for playback?)

        listener.getLogger().println("Launching FAST for recording...");

        String docker_id = "no docker id was retrieved";

        try {
            docker_id = shell_command(launcher, cmd);
        }
        catch (java.io.IOException | java.lang.InterruptedException error) {
            listener.getLogger().println("Failed to get docker id:");
            listener.getLogger().println(docker_id);
        }

        if (docker_id == null || docker_id.contains("Error")) {
            listener.getLogger().println(docker_id);
            throw new AbortException("Cannot start FAST docker due to docker conflict");
        }

        listener.getLogger().println("Waiting for ready status");
        List<String> cmd_for_health = new ArrayList<String>();
        cmd_for_health.add("docker exec -t");
        cmd_for_health.add(docker_id);
        cmd_for_health.add("supervisorctl status proxy");

        String health;
        for (int i = 0; i < 10; i++) {
            try {
                health = shell_command(launcher, cmd_for_health);
            }
            catch (java.io.IOException | java.lang.InterruptedException error) {
                listener.getLogger().println("Failed to get docker status:");
                listener.getLogger().println(docker_id);
                throw new AbortException("Health check exited with exception");
            }

            listener.getLogger().println("health check: " + health);

            if (health.contains("RUNNING")) { break; }

            Thread.sleep(10000);

            if (i < 9) { continue; }

            List<String> kill_cmd = new ArrayList<String>();
            kill_cmd.add("docker kill");
            kill_cmd.add(docker_id);
            execute_cmd(launcher, listener, kill_cmd);
            throw new AbortException("Cannot start FAST docker due to timeout on proxy");
        }

        listener.getLogger().println("FAST is ready to record");

        // No cleanup here.
        // If next step starts, then we get a chance to kill existing dockers.
        // Otherwise it will be hanging
    }


    public void run_tests(
            List<String> cmd,
            Run<?,?> build,
            Launcher launcher,
            TaskListener listener) throws IOException, InterruptedException {
        // there may be a running fast recorder
        // we have no way of knowing if one exists,
        // or what name it has

        int test_run_status = -1;

        try {
            listener.getLogger().println("Starting Wallarm FAST tests");
            test_run_status = execute_cmd(
                launcher,
                listener,
                cmd);

            listener.getLogger().println("Test run status: " + test_run_status);
            listener.getLogger().println("Finishing Wallarm FAST tests...");

            if (test_run_status != 0) {
                if (failBuild) {
                  throw new AbortException("Security tests failed! Build set to fail");
                } else {
                  listener.getLogger().println("Security tests failed! Build set to not fail");
                }
            } else {
                listener.getLogger().println("Security tests passed!");
            }
        }
        catch (AbortException error)
        {
            throw new AbortException("Security tests failed! Build set to fail");
        }
        catch (java.io.IOException error)
        {
            listener.getLogger().println("Cannot get build env params: " + error);
        }

    }

    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {
        private Secret  wallarmApiToken;
        private String  appHost;
        private String  appPort;
        private String  fastPort;
        private String  fastName;
        private int     policyId;
        private int     testRecordId;
        private String  wallarmApiHost;
        private String  testRunName;
        private String  testRunDesc;
        private boolean record;
        private boolean stopOnFirstFail;
        private boolean failBuild;
        private boolean withoutSudo;
        private String  localDockerNetwork;
        private String  localDockerIp;
        private String  wallarmVersion;
        private String  fileExtensionsToExclude;
        private int     inactivityTimeout;
        private int     testRunRps;


        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            req.bindJSON(this, formData);
            save();
            return super.configure(req,formData);
        }

        public String getWallarmApiToken() {
            return wallarmApiToken.getPlainText();
        }

        public String getAppHost() {
            return appHost;
        }

        public String getAppPort() {
            return appPort;
        }

        public String getFastPort() {
            return fastPort;
        }

        public String getFastName() {
            return fastName;
        }

        public int getPolicyId() {
            return policyId;
        }

        public int getTestRecordId() {
            return testRecordId;
        }

        public String getWallarmApiHost() {
            return wallarmApiHost;
        }

        public String getTestRunName() {
            return testRunName;
        }

        public String getTestRunDesc() {
            return testRunDesc;
        }

        public boolean getRecord() {
            return record;
        }

        public boolean getStopOnFirstFail() {
            return stopOnFirstFail;
        }

        public boolean getFailBuild() {
            return failBuild;
        }

        public boolean getWithoutSudo() {
            return withoutSudo;
        }

        public String getLocalDockerNetwork() {
            return localDockerNetwork;
        }

        public String getLocalDockerIp() {
            return localDockerIp;
        }

        public String getWallarmVersion() {
            return wallarmVersion;
        }

        public String getFileExtensionsToExclude() {
            return fileExtensionsToExclude;
        }

        public int getInactivityTimeout() {
            return inactivityTimeout;
        }

        public int getTestRunRps() {
            return testRunRps;
        }

    }

}
