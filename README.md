Setup your environment
----------------------

1.  Compile and install the dependencies.

    You need first to install the m2e-flexmojos-runtime pom.xml, this maven module contain the dependencies required by the connector.
    If you have not already fetched the git submodule required (m2e-core), run the following command in the project root:
    <pre>git submodule update</pre>
    Then, install the maven artifact by running: <pre>mvn -f m2e-flexmojos-runtime/pom.xml clean install</pre>

2.  Create the FlashBuilder 4.x target platform as a p2 repository.

    The UpdateSite Publisher Application (org.eclipse.equinox.p2.publisher.UpdateSitePublisher) is a headless application that is capable of generating metadata (p2 repositories) from an update site containing a site.xml, bundles and features.
    
    In eclipse, create a new configuration: Run > Run Configurations... Select Eclipse Application.
    In the fieldset "Program to Run" select "Run an application" and choose org.eclipse.equinox.p2.publisher.FeaturesAndBundlesPublisher.
    In the Arguments tab, put the following in the text area "Program arguments":

        -metadataRepository file:<some location>\repository
        -artifactRepository file:<some location>\repository
        -source <location with a plugin and feature directory>;
        -configs gtk.linux.x86
        -compress
        -publishArtifacts

    Where <some location> is your repository root and <location with a plugin and feature directory> is your Flash Builder product.
    Run the configuration and repeat the step 2 for each Flash Builder installation you have: 4.0, 4.5, 4.6 and 4.7.

    Note: you can have more information on creating a p2 repository in http://wiki.eclipse.org/Equinox/p2/Publisher#Features_And_Bundles_Publisher_Application.

3.  Compile the project.

    Add the repository you created in your .m2/settings.xml in a profile as follow:

        <profiles>
          <profile>
            <id>flash-builder-4x</id>
            <repositories>
              <repository>
                <id>fb4x</id>
                <layout>p2</layout>
                <url>file:/<some location>\repository</url>
              </repository>
            </repositories>
          </profile>
          
          ...
          
        </profiles>

    Where 'x' is the minor version of the Flash Builder product you want to compile against.

    Do not forget to use this profile when invoking maven on the project! It will allows Tycho to use the FlashBuilder 4.x platform when building the m2e connector plugin. Note that you will also need to set-up a new Target Definition in eclipse to use the plugins and features of your FlashBuilder installation.
