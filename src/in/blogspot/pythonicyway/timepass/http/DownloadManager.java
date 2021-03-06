package in.blogspot.pythonicyway.timepass.http;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by karan.khosla
 */
public class DownloadManager {

  private static final DownloadManager INSTANCE = new DownloadManager();

  private DownloadManager() {
  }

  public static DownloadManager getInstance() {
    return INSTANCE;
  }

  private HttpURLConnection getConnection(final URL connectionURL) throws IOException {
    return (HttpURLConnection) connectionURL.openConnection();
  }

  private HttpURLConnection connectTo(final URL newURL) throws IOException {
    final HttpURLConnection connection = getConnection(newURL);
    beforeConnectionEnableBasicHTTPProperties(connection);
    connection.connect();
    printServerTime(connection);
    return connection;
  }

  private HttpURLConnection connectTo(final String url) throws IOException {
    return connectTo(new URL(url));
  }

  private void beforeConnectionEnableBasicHTTPProperties(final HttpURLConnection connection) {
    connection.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0; .NET CLR 2.0.50727; MS-RTC LM 8)");
    connection.setInstanceFollowRedirects(true);
    connection.setUseCaches(false);
    connection.setDoInput(true);
    connection.setDoOutput(true);
  }

  public void basicFileDownload(String url, String destinationFileName) {
    HttpURLConnection connection;
    DataInputStream readFromSourceStream = null;
    DataOutputStream writeToDestinationStream = null;
    try {
      connection = connectTo(url);
      readFromSourceStream = new DataInputStream(connection.getInputStream());
      writeToDestinationStream = new DataOutputStream(new FileOutputStream(destinationFileName));
      int bytesRead;
      while ((bytesRead = readFromSourceStream.read()) != -1)
        writeToDestinationStream.write(bytesRead);
      connection.disconnect();
    } catch (IOException e) {
      System.out.println("Please Try again if you're seeing this error.\n" +
              "Sometimes there are problems connecting to the site." +
              "There can also be a problem with our destination path" +
              "Try to download & change save path on the disk" +
              "Hope it will solve your problem." + e.getMessage());
    } finally {
      safeClose(readFromSourceStream);
      safeClose(writeToDestinationStream);
    }
  }

  private void printServerTime(HttpURLConnection connection) {
    SimpleDateFormat serverDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a z");
    System.out.println("DateTime: " + serverDateFormat.format(new Date(connection.getDate())));
  }

  public String downloadHTMLFromURL(String url) {
    StringBuilder buildHTMLContent = new StringBuilder();
    HttpURLConnection connection;
    try {
      connection = connectTo(url);
      DataInputStream readFromSourceStream = new DataInputStream(connection.getInputStream());
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(readFromSourceStream));
      String dataReadFromStream;
      while ((dataReadFromStream = bufferedReader.readLine()) != null)
        buildHTMLContent.append(dataReadFromStream);
    } catch (IOException e) {
      System.out.println("Please Try again if you're seeing this error.\n" +
              "Sometimes there are problems connecting to the site." +
              "There can also be a problem with our destination path" +
              "Try to download & change save path on the disk" +
              "Hope it will solve your problem." + e.getMessage());
    }
    return buildHTMLContent.toString();
  }

  private void safeClose(Closeable closeable) {
    try {
      closeable.close();
    } catch (IOException ignored) {
    }
  }
}
