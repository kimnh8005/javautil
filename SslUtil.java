package md.util;

import java.net.URL;
import javax.servlet.http.HttpServletRequest;

public class SslUtil{

	private static final int DEFAULT_SSL_PORT = 443;
	 
    public SslUtil(){}

    public static String toSslUrl(HttpServletRequest request, String uri)
        throws Exception
    {
        URL url = new URL(request.getRequestURL().toString());
        String httpsServerUrl = (new StringBuilder("https://")).append(getHostStr(request, uri)).toString();
        return recompositeUrl(request, httpsServerUrl, uri);
    }

    public static String toHttpUrl(HttpServletRequest request, String uri)
        throws Exception
    {
        URL url = new URL(request.getRequestURL().toString());
        String httpServerUrl = (new StringBuilder("http://")).append(getHostStr(request, uri)).toString();
        return recompositeUrl(request, httpServerUrl, uri);
    }

    private static String getHostStr(HttpServletRequest request, String uri)
        throws Exception
    {
        URL url = new URL(request.getRequestURL().toString());
        return (new StringBuilder(String.valueOf(url.getHost()))).append(url.getPort() == 80 || url.getPort() == 443 ? "" : (new StringBuilder(":")).append(url.getPort()).toString()).toString();
    }

    private static String recompositeUrl(HttpServletRequest request, String serverUrl, String uri)
    {
        if(uri.length() > 0 && uri.charAt(0) == '/') {
            return (new StringBuilder(String.valueOf(serverUrl))).append(uri).toString();
        } else {
            String currPath = (new StringBuilder(String.valueOf(serverUrl))).append(request.getServletPath()).toString();
            currPath = currPath.substring(0, currPath.lastIndexOf("/"));
            return (new StringBuilder(String.valueOf(currPath))).append('/').append(uri).toString();
        }
    }
   
}
