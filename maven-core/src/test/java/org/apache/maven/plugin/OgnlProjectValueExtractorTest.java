package org.apache.maven.plugin;

import org.apache.maven.MavenTestCase;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.lifecycle.MavenLifecycleContext;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;

import java.io.File;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class OgnlProjectValueExtractorTest
    extends MavenTestCase
{
    private MavenProject project;

    private MavenProjectBuilder builder;

    private MavenLifecycleContext context;

    protected void setUp()
        throws Exception
    {
        super.setUp();

        builder = (MavenProjectBuilder) lookup( MavenProjectBuilder.ROLE );

        File f =  new File( basedir, "src/test/resources/pom.xml" );

        project = builder.build( f, new ArtifactRepository() );

        project.setProperty( "foo", "bar" );

        context = new MavenLifecycleContext( getContainer(), project, null, new ArtifactRepository( "foo", "http://bar" ) );
    }

    public void testPropertyValueExtraction()
    {
        Object value = OgnlProjectValueExtractor.evaluate( "#foo", context );

        assertEquals( "bar", value );
    }

    public void testValueExtractionWithAPropertyContainingAPath()
    {
        Object value = OgnlProjectValueExtractor.evaluate( "#foo/META-INF/maven", context );

        assertEquals( "bar/META-INF/maven", value );
    }

    public void testValueExtractionWithAPomValueContainingAPath()
        throws Exception
    {
        Object value = OgnlProjectValueExtractor.evaluate( "#project.build.directory/classes", context );

        String expected = new File( basedir, "src/test/resources/target/classes" ).getCanonicalPath();

        String actual = new File( value.toString() ).getCanonicalPath();

        assertEquals( expected, actual );
    }

    public void testParameterThatIsAComponent()
        throws Exception
    {
        String role = "#component.org.apache.maven.project.MavenProjectBuilder";

        Object value = OgnlProjectValueExtractor.evaluate( role, context );

        assertNotNull( value );
    }

    public void testLocalRepositoryExtraction()
        throws Exception
    {
        Object value = OgnlProjectValueExtractor.evaluate( "#localRepository", context );

        assertEquals( "foo", ((ArtifactRepository)value).getId() );
    }
}
