package com.github.trecloux.yeoman;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;

/*
 * Copyright 2013 Thomas Recloux
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
@Mojo( name = "build", defaultPhase = LifecyclePhase.PREPARE_PACKAGE )
public class YeomanMojo extends AbstractMojo {
    @Parameter( defaultValue = "yo", required = true )
    File yeomanProjectDirectory;
    @Parameter( defaultValue = "${os.name}", readonly = true)
    String osName;

    public void execute() throws MojoExecutionException {
        npmInstall();
        bowerInstall();
        grunt();
    }

    private void npmInstall() throws MojoExecutionException {
        logToolVersion("node");
        logToolVersion("npm");
        logAndExecuteCommand("npm install");
    }

    private void bowerInstall() throws MojoExecutionException {
        logToolVersion("bower");
        logAndExecuteCommand("bower install --no-color");
    }
    private void grunt() throws MojoExecutionException {
        logToolVersion("grunt");
        logAndExecuteCommand("grunt --no-color");
    }

    private void logToolVersion(final String toolName) throws MojoExecutionException {
        getLog().info(toolName + " version :");
        executeCommand(toolName + " --version");
    }

    private void logAndExecuteCommand(String command) throws MojoExecutionException {
        logCommand(command);
        executeCommand(command);
    }

    private void logCommand(String command) {
        getLog().info("--------------------------------------");
        getLog().info("         " + command.toUpperCase());
        getLog().info("--------------------------------------");
    }

    private void executeCommand(String command) throws MojoExecutionException {
        try {
            if (isWindows()) {
                command = "cmd /c " + command;
            }
            CommandLine cmdLine = CommandLine.parse(command);
            DefaultExecutor executor = new DefaultExecutor();
            executor.setWorkingDirectory(yeomanProjectDirectory);
            executor.execute(cmdLine);
        } catch (IOException e) {
            throw new MojoExecutionException("Error during : " + command, e);
        }
    }


    private boolean isWindows() {
        return osName.startsWith("Windows");
    }
}
