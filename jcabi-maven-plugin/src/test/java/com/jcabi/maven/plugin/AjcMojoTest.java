/**
 * Copyright (c) 2012-2013, JCabi.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the jcabi.com nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jcabi.maven.plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.project.MavenProject;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

/**
 * Test case for {@link AjcMojo}.
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 */
public final class AjcMojoTest extends AbstractMojoTestCase {

    /**
     * Temp dir.
     * @checkstyle VisibilityModifier (3 lines)
     */
    public final transient TemporaryFolder temp = new TemporaryFolder();

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setUp() throws Exception {
//        super.setUp();
        this.temp.create();
    }

    /**
     * AjcMojo can weave class files with aspects.
     * @throws Exception If something is wrong
     */
    public void testClassFilesWeaving() throws Exception {
//        final AjcMojo mojo = AjcMojo.class.cast(
//            this.lookupMojo(
//                "ajc",
//                this.getClass().getResource("ajc-pom.xml").getFile()
//            )
//        );
        final AjcMojo mojo = new AjcMojo();
        final MavenProject project = Mockito.mock(MavenProject.class);
        Mockito.doReturn(new ArrayList<String>())
            .when(project).getCompileClasspathElements();
        final File temps = this.temp.newFolder();
        final File classes = this.temp.newFolder();
        final File javas = this.temp.newFolder();
        this.setVariableValueToObject(mojo, "project", project);
        this.setVariableValueToObject(mojo, "classesDirectory", classes);
        this.setVariableValueToObject(mojo, "aspectDirectories", new File[] {});
        this.setVariableValueToObject(mojo, "tempDirectory", temps);
        final File java = new File(javas, "Foo.java");
        FileUtils.write(
            java,
            "import com.jcabi.aspects.Immutable; @Immutable class Foo {}"
        );
        final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        final StandardJavaFileManager mgr = compiler.getStandardFileManager(
            null, Locale.ENGLISH, Charsets.UTF_8
        );
        compiler.getTask(
            null, mgr, null, null, null,
            mgr.getJavaFileObjectsFromFiles(Arrays.asList(java))
        ).call();
        mgr.close();
        FileUtils.copyFileToDirectory(new File(javas, "Foo.class"), classes);
        mojo.execute();
        MatcherAssert.assertThat(
            temps.listFiles(),
            Matchers.not(Matchers.emptyArray())
        );
    }

}
