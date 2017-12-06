# ch-inofix-timetracker

<a href="https://travis-ci.org/inofix/ch-inofix-timetracker/builds" target="_blank"><img src="https://travis-ci.org/inofix/ch-inofix-timetracker.svg?branch=master"/></a>

A timetracker plugin for Liferay.

## How To Build

### Commandline

1. Install blade: `curl https://raw.githubusercontent.com/liferay/liferay-blade-cli/master/installers/local | sh`
1. Create a liferay workspace: `WORKSPACE=my-workspace; blade init $WORKSPACE`
1. Checkout timetracker sources to the workspace's module directory: `cd $WORKSPACE/modules; git clone https://github.com/inofix/ch-inofix-timetracker.git`
1. Change working directory to ch-inofix-timetracker: `cd $WORKSPACE/modules/ch-inofix-timetracker`
1. Run ServiceBuilder: `./gradlew buildService`
1. Build: `./gradlew jar`

### IDE

1. Download latest IDE from Sourceforge: https://sourceforge.net/projects/lportal/files/Liferay%20IDE/3.1.2%20GA3/liferay-ide-eclipse-linux-x64-3.1.2-ga3-201709011126.tar.gz/download
1. `tar -xvzf liferay-ide-eclipse-linux-x64-3.1.2-ga3-201709011126.tar.gz`
1. `mv eclipse liferay-ide-3.1.2`
1. Start IDE with: `./liferay-ide-3.1.2/eclipse &`
1. Create Eclipse Workspace: `~/workspace`
1. Close welcome screen
1. File -> New-> Project -> Liferay Workspace Project
1. Project name: `my-liferay-project`, Use default location: yes (default), Build Type: Gradle (default) 
1. Finish
1. Update liferay.workspace.bundle.url in gradle.properties. Use: liferay.workspace.bundle.url=https://cdn.lfrs.sl/releases.liferay.com/portal/7.0.4-ga5/liferay-ce-portal-tomcat-7.0-ga5-20171018150113838.zip
1. Switch to Gradle Tasks view
1. Expand workspace node
1. Expand bundle node
1. run `initBundle`
1. Switch to Servers view
1. New Server -> Liferay 7.x -> Next
1. Name: Liferay 7.x
1. Liferay Portal Bundle Directory: $WORKSPACE/bundles
1. Select runtime JRE: java-8-openjdk-amd64
1. Finish
1. Doubleclick configured server (Liferay 7.x., see above), edit Launch settings: 
1. Liferay Launch: Custom Launche Settings: yes;  Use developer mode: yes
1. Save
1. Start server

## How To Contribute
1. Fork this repository to your individual github account.
1. Clone your personal fork to your local liferay worspace: `cd $WORKSPACE/modules/`
1. `git clone https://github.com/<your-personal-github-account>/ch-inofix-timetracker`
1. Use feature branches to work on new features or known issues.
1. Merge finished features into your individual master branch and 
1. create pull-requests, to contribute your solutions to the inofix master branch.

**Stay up-to-date**

1. Change the current working directory to your local project.
1. Configure https://github.com/inofix/ch-inofix-timetracker as additional upstream remote (see: https://help.github.com/articles/configuring-a-remote-for-a-fork/)
1. Sync inofix-master with your individual fork (see: https://help.github.com/articles/syncing-a-fork/): 
1. Fetch upstream/master to your local copy: `git fetch upstream` 
1. Check out your fork's local 'master' branch: `git checkout master`
1. Merge the changes from 'upstream/master' into your local master branch. This brings your fork's master branch into sync with the upstream repository, without losing your local changes: `git merge upstream/master`
1. Push merged master to your individual github account and 
1. create a pull-request, to contribute your solution to the inofix master branch.

**Note for Eclipse / Liferay-IDE developers**

If the JSPs of the timetracker-web project aren't validated properly, 

- select the timetracker-web in the Project-Explorer view
- open the context menu with the right mouse key
- select "Configure" and
- choose "Add JSP Validation Support"
- select the timetracker-web in the Project-Explorer view
- choose CTRL + F5 to refresh your project

You may have to restart Eclipse, too in order to have your JSPs validated.

## How To Test

### In the Liferay workspace

1. Select ch-inofix-timetracker -> verification -> testIntegration task from Gradle Tasks
1. Run

### Standalone 

1. `cd ch-inofix-timetracker`
1. `ln -s standalone-gradle.properties gradle.properties`
1. `ln -s standalone-settings.gradle settings.gradle`
1. `./gradlew clean initBundle buildService testIntegration`

Latest Travis-test-results for ch-inofix-timetracker can be obtained from https://travis-ci.org/inofix/ch-inofix-timetracker/builds
