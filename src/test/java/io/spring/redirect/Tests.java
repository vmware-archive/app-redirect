package io.spring.redirect;

import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.tuckey.web.filters.urlrewrite.UrlRewriteFilter;

import javax.servlet.ServletException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Ignore
public class Tests {

    private UrlRewriteFilter filter;

    @Before
    public void setUp() throws Exception {
        MockFilterConfig filterConfig = new MockFilterConfig(Application.REWRITE_FILTER_NAME);
        filterConfig.addInitParameter("confPath", Application.REWRITE_FILTER_CONF_PATH);
        filterConfig.addInitParameter("logLevel", "WARN");

        filter = new UrlRewriteFilter();
        filter.init(filterConfig);
    }

    @Test
    public void rootRedirectsToRules() throws Exception {
        validateTemporaryRedirect("http://redirect.spring.io", "/rules");
        validateTemporaryRedirect("http://redirect.spring.io/", "/rules");
    }

    @Test
    public void legacySiteDocrootsAreRedirected() throws Exception {
        validatePermanentRedirect("http://springsource.org", "http://spring.io");
        validatePermanentRedirect("http://www.springsource.org", "http://spring.io");
        validatePermanentRedirect("http://springsource.com/", "http://spring.io");
        validatePermanentRedirect("http://www.springsource.com/", "http://spring.io");
        validatePermanentRedirect("http://springframework.org", "http://spring.io");
        validatePermanentRedirect("http://www.springframework.org", "http://spring.io");
        validatePermanentRedirect("http://springframework.org/", "http://spring.io");
        validatePermanentRedirect("http://www.springframework.org/", "http://spring.io");
    }

    @Test
    public void ebrRequestsAreRedirected() throws Exception {
        validatePermanentRedirect("http://www.springsource.com/repository", "http://ebr.springsource.com/repository");
        validatePermanentRedirect("http://www.springsource.com/repository/anything", "http://ebr.springsource.com/repository/anything");
    }

    @Test
    public void interface21Redirects() throws Exception {
        validatePermanentRedirect("http://interface21.com", "http://spring.io");
        validatePermanentRedirect("http://www.interface21.com", "http://spring.io");
        validatePermanentRedirect("http://blog.interface21.com", "http://spring.io/blog");
    }

    @Test
    public void legacyStsGgtsRedirects() throws Exception {
        validatePermanentRedirect("http://www.springsource.org/groovy-grails-tool-suite-download", "http://spring.io/tools/ggts");
        validatePermanentRedirect("http://www.springsource.org/sts/welcome", "http://spring.io/tools/sts/welcome");
        validateTemporaryRedirect("http://www.springsource.org/ggts/welcome", "http://grails.org/products/ggts/welcome");
        validatePermanentRedirect("http://www.springsource.org/springsource-tool-suite-download", "http://spring.io/tools/sts");
        validatePermanentRedirect("http://www.springsource.org/downloads/sts-ggts", "http://spring.io/tools");
    }

    @Test
    public void legacyBlogSiteRequestsAreRedirected() throws Exception {
        validatePermanentRedirect("http://blog.springsource.org", "http://spring.io/blog/");
        validatePermanentRedirect("http://blog.springsource.org/", "http://spring.io/blog/");
        validatePermanentRedirect("http://blog.springsource.com", "http://spring.io/blog/");
        validatePermanentRedirect("http://blog.springsource.com/", "http://spring.io/blog/");
    }

    @Test
    public void legacyBlogFeedRequestsAreRedirected() throws Exception {
        validateTemporaryRedirect("http://spring.io/blog/feed", "http://spring.io/blog.atom");
        validateTemporaryRedirect("http://spring.io/blog/feed/", "http://spring.io/blog.atom");
        validateTemporaryRedirect("http://spring.io/blog/feed/atom/", "http://spring.io/blog.atom");
        validateTemporaryRedirect("http://spring.io/blog/category/security/feed/", "http://spring.io/blog.atom");
        validateTemporaryRedirect("http://spring.io/blog/main/feed/", "http://spring.io/blog.atom");
    }

    @Test
    public void legacyBlogResourceRequestsAreRedirectedToOldBlog() throws Exception {
        validateTemporaryRedirect("http://blog.springsource.org/wp-content/plugins/syntaxhighlighter/syntaxhighlighter2/scripts/shBrushJava.js",
                "http://assets.spring.io/wp/wp-content/plugins/syntaxhighlighter/syntaxhighlighter2/scripts/shBrushJava.js");
    }

    @Test
    public void legacyBlogPostRequestsAreRedirected() throws Exception {
        validatePermanentRedirect("http://blog.springsource.org/blog/2013/07/24/spring-framework-4-0-m2-websocket-messaging-architectures",
                "http://spring.io/blog/blog/2013/07/24/spring-framework-4-0-m2-websocket-messaging-architectures");
    }

    @Test
    public void legacySTSBlogFeedRequestsAreRedirectedToOldBlog() throws Exception {
        validateTemporaryRedirect("http://blog.springsource.com/main/feed/", "http://assets.spring.io/wp/feed.xml");
    }

    @Test
    public void blogPagesAreRedirected() throws Exception {
        validatePermanentRedirect("http://blog.springsource.org/anything", "http://spring.io/blog/anything");
        validatePermanentRedirect("http://blog.springsource.org/anything", "http://spring.io/blog/anything");
    }

    @Test
    public void blogAssetsAreRedirected() throws Exception {
        validateTemporaryRedirect("http://blog.springsource.org/wp-content/uploads/attachment.zip", "http://assets.spring.io/wp/wp-content/uploads/attachment.zip");
    }

    @Test
    public void blogAuthorsAreRedirected() throws Exception {
        validatePermanentRedirect("http://blog.springsource.org/author/cbeams", "http://spring.io/team/cbeams");

        validatePermanentRedirect("http://blog.springsource.org/author/benh", "http://spring.io/team/bhale");
        validatePermanentRedirect("http://blog.springsource.org/author/juergenh", "http://spring.io/team/jhoeller");
        validatePermanentRedirect("http://blog.springsource.org/author/markf", "http://spring.io/team/mfisher");
        validatePermanentRedirect("http://blog.springsource.org/author/chapmanp", "http://spring.io/team/pchapman");
        validatePermanentRedirect("http://blog.springsource.org/author/adrianc", "http://spring.io/team/acolyer");
        validatePermanentRedirect("http://blog.springsource.org/author/arjenp", "http://spring.io/team/apoutsma");
        validatePermanentRedirect("http://blog.springsource.org/author/jolong", "http://spring.io/team/jlong");

        validatePermanentRedirect("http://blog.springsource.com/author/jolong", "http://spring.io/team/jlong");

        validatePermanentRedirect("http://blog.springsource.org/author", "http://spring.io/team");
        validatePermanentRedirect("http://blog.springsource.org/author/", "http://spring.io/team/");
    }

    @Test
    public void drupalNodesAreRedirected() throws Exception {
        validatePermanentRedirect("http://www.springsource.org/node/3762", "http://spring.io/blog/2012/12/10/spring-framework-3-2-ga-released");
        validatePermanentRedirect("http://www.springsource.org/BusinessIntelligenceWithSpringAndBIRT", "http://spring.io/blog/2012/01/30/spring-framework-birt");
        validatePermanentRedirect("http://www.springsource.org/node/22687", "http://forum.spring.io/forum/spring-projects/data/hadoop/131011-spring-for-apache-hadoop-1-0-1-ga-and-2-0-m1-released");
    }

    @Test
    public void drupalThemeImagesAreRedirected() throws Exception {
        validateTemporaryRedirect("http://www.springsource.org/sites/all/themes/s2org11/favicon.ico", "http://assets.spring.io/drupal/sites/all/themes/s2org11/favicon.ico");
    }

    @Test
    public void oldCaseStudiesAreRedirected() throws Exception {
        validateTemporaryRedirect("http://www.springsource.org/files/uploads/file.pdf", "http://assets.spring.io/drupal/files/uploads/file.pdf");
    }

    @Test
    public void projectPagesAreRedirected() throws ServletException, IOException, URISyntaxException {
        validatePermanentRedirect("http://springsource.org/spring-amqp", "http://projects.spring.io/spring-amqp");
        validatePermanentRedirect("http://www.springsource.org/spring-amqp", "http://projects.spring.io/spring-amqp");
    }

    @Test
    public void legacySchemaRequestsAreRedirected() throws Exception {
        for (String host : new String [] { "springsource.org", "www.springsource.org",
                "springframework.org", "www.springframework.org"}) {
            validatePermanentRedirect("http://" + host + "/schema", "http://schema.spring.io");
            validatePermanentRedirect("http://" + host + "/schema/", "http://schema.spring.io/");
            validatePermanentRedirect("http://" + host + "/schema/oxm", "http://schema.spring.io/oxm");
            validatePermanentRedirect("http://" + host + "/schema/oxm/spring-oxm.xsd",
                    "http://schema.spring.io/oxm/spring-oxm.xsd");
        }
    }

    @Test
    public void legacyDTDRequestsAreRedirected() throws Exception {
        for (String host : new String [] { "springsource.org", "www.springsource.org",
                                           "springframework.org", "www.springframework.org"}) {
            validatePermanentRedirect("http://" + host + "/dtd", "http://schema.spring.io/dtd");
            validatePermanentRedirect("http://" + host + "/dtd/", "http://schema.spring.io/dtd/");
            validatePermanentRedirect("http://" + host + "/dtd/spring-beans-2.0.dtd", "http://schema.spring.io/dtd/spring-beans-2.0.dtd");
        }
    }

    @Test
    public void legacyStaticDocsRequestsAreRedirected() throws Exception {
        for (String host : new String [] { "static.springsource.org", "static.springframework.org" }) {
            validateTemporaryRedirect("http://" + host + "", "http://spring.io/docs");
            validateTemporaryRedirect("http://" + host + "/", "http://spring.io/docs");
            validatePermanentRedirect("http://" + host + "/spring-anything", "http://docs.spring.io/spring-anything");
        }
    }

    @Test
    public void legacyForumRequestsAreRedirected() throws Exception {
        validatePermanentRedirect("http://forum.springsource.org", "http://forum.spring.io/");
        validatePermanentRedirect("http://forum.springframework.org", "http://forum.spring.io/");
        // something is stripping the query strings during testing, but this actually
        // works against the running site
        // validatePermanentRedirect("http://forum.springsource.org/showthread.php?48738-Getting-Spring-to-throw-duplicate-bean-definition-exception",
        //                           "http://forum.spring.io/showthread.php?48738-Getting-Spring-to-throw-duplicate-bean-definition-exception");
        validatePermanentRedirect("http://forum.springsource.org/showthread.php?48738-Getting-Spring-to-throw-duplicate-bean-definition-exception",
                                  "http://forum.spring.io/showthread.php");
    }

    @Test
    public void inboundLinksFromEclipseMarketplaceAreRedirected() throws Exception {
        validateTemporaryRedirect("http://www.springsource.org/eclipse-downloads", "http://spring.io/tools/eclipse");
        validateTemporaryRedirect("http://www.springsource.com/products/eclipse-downloads", "http://spring.io/tools/eclipse");
    }

    @Test
    public void legacyLinkedInPathIsRedirected() throws Exception {
        validateTemporaryRedirect("http://www.springsource.org/linkedin", "http://www.linkedin.com/groups/Spring-Users-46964?gid=46964");
    }

    @Test
    public void legacyDownloadRequestsAreRedirectedToProjectsPage() throws Exception {
        validateTemporaryRedirect("http://www.springsource.org/download", "http://spring.io/projects");
        validateTemporaryRedirect("http://www.springsource.org/download/community", "http://spring.io/projects");
        validateTemporaryRedirect("http://www.springsource.org/spring-community-download", "http://spring.io/projects");
        validateTemporaryRedirect("http://www.springsource.org/springsource-community-download", "http://spring.io/projects");
        validateTemporaryRedirect("http://www.springsource.org/spring-community-download?utm_source=eclipse.org&utm_medium=web&utm_content=promotedDL&utm_campaign=STS",
                                  "http://spring.io/projects");
    }

    @Test
    public void ldapIsRedirected() throws Exception {
        validatePermanentRedirect("http://www.springsource.org/ldap", "http://projects.spring.io/spring-ldap");
        validatePermanentRedirect("http://springsource.org/ldap", "http://projects.spring.io/spring-ldap");
    }

    @Test
    public void gemfireIsRedirected() throws Exception {
        validatePermanentRedirect("http://www.springsource.org/spring-gemfire", "http://projects.spring.io/spring-data-gemfire");
    }

    @Test
    public void rooIsRedirected() throws Exception {
        validateTemporaryRedirect("http://www.springsource.org/roo", "https://github.com/spring-projects/spring-roo");
        validateTemporaryRedirect("http://www.springsource.org/spring-roo", "https://github.com/spring-projects/spring-roo");
    }

    @Test
    public void legacyNewsAndEventsFeedRequestsAreRedirected() throws Exception {
        validateTemporaryRedirect("http://www.springsource.org/node/feed", "http://assets.spring.io/drupal/node/feed.xml");
        validateTemporaryRedirect("http://www.springsource.org/newsblog/feed", "http://assets.spring.io/wp/feed.xml");
    }

    @Test
    public void legacyDrupalFilesAreRedirected() throws Exception {
        validateTemporaryRedirect("http://www.springsource.org/files/SpringOne/2013/training-bg-image.png", "http://assets.spring.io/drupal/files/SpringOne/2013/training-bg-image.png");
        validateTemporaryRedirect("http://www.springsource.org/files/other.jpg", "http://assets.spring.io/drupal/files/other.jpg");
    }

    @Test
    public void webflowIsRedirected() throws Exception {
        validatePermanentRedirect("http://www.springsource.org/spring-webflow", "http://projects.spring.io/spring-webflow");
        validatePermanentRedirect("http://www.springsource.org/spring-web-flow", "http://projects.spring.io/spring-webflow");
        validatePermanentRedirect("http://www.springsource.org/webflow", "http://projects.spring.io/spring-webflow");
        validatePermanentRedirect("http://www.springsource.org/go-webflow", "http://projects.spring.io/spring-webflow");
        validatePermanentRedirect("http://www.springsource.org/webflow-samples", "http://projects.spring.io/spring-webflow");
        validatePermanentRedirect("http://www.springsource.org/webflow-1.0", "http://projects.spring.io/spring-webflow");
    }

    @Test
    public void misc() throws Exception {
        validatePermanentRedirect("http://www.springsource.org/news-events", "http://spring.io/blog/category/news");
        validatePermanentRedirect("http://www.springsource.org/consulting", "http://spring.io/services");
    }

    @Test
    public void legacySupportRequestsAreRedirected() throws Exception {
        validateTemporaryRedirect("http://springsource.org/support", "http://www.gopivotal.com/contact/spring-support");
        validateTemporaryRedirect("http://springsource.com/support", "http://www.gopivotal.com/contact/spring-support");
    }

    private void validateTemporaryRedirect(String requestedUrl, String redirectedUrl) throws IOException, ServletException, URISyntaxException {
        validateRedirect(requestedUrl, redirectedUrl, 302);
    }

    private void validatePermanentRedirect(String requestedUrl, String redirectedUrl) throws IOException, ServletException, URISyntaxException {
        validateRedirect(requestedUrl, redirectedUrl, 301);
    }

    private void validateRedirect(String requestedUrl, String redirectedUrl, int expectedStatus) throws IOException, ServletException, URISyntaxException {
        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        URI requestUrl = new URI(requestedUrl);
        if (requestUrl.getHost() != null) {
            servletRequest.addHeader("host", requestUrl.getHost());
        }
        servletRequest.setRequestURI(requestUrl.getPath());
        MockHttpServletResponse servletResponse = new MockHttpServletResponse();

        filter.doFilter(servletRequest, servletResponse, null);

        assertThat(servletResponse.getRedirectedUrl(), equalTo(redirectedUrl));
        assertThat("Incorrect status code for " + redirectedUrl, servletResponse.getStatus(), equalTo(expectedStatus));
    }
}
