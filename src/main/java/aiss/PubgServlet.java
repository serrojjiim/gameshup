package aiss;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.DeserializationConfig.Feature;

import org.restlet.data.Header;
import org.restlet.engine.header.HeaderConstants;
import org.restlet.resource.ClientResource;
import org.restlet.util.Series;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import pubgmatch.Stats;
import pubgplayer.PlayerPubg;
import pubgseason.PubgSeason;


/**
 * Servlet implementation class PubgServlet
 */
public class PubgServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public PubgServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
			try {
				doTrustToCertificates();
				ObjectMapper objectMapper = new ObjectMapper();
				

				URL url = new URL("https://api.pubg.com/shards/steam/players?filter[playerNames]="+request.getParameter("name"));
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				conn.setRequestProperty("Authorization","Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJiMDNhZWE2MC0zNmNlLTAxMzgtYmJjOS0zNzRkM2UxZGEzNjYiLCJpc3MiOiJnYW1lbG9ja2VyIiwiaWF0IjoxNTgyMjg2MDA0LCJwdWIiOiJibHVlaG9sZSIsInRpdGxlIjoicHViZyIsImFwcCI6InNlcmdpb3JvamFzamltIn0.dFS0GuKAPpTrOEChROMqc3APivDw-NDbwAhDpK4WMT8");
				conn.setRequestProperty("Accept", "application/vnd.api+json");
				PlayerPubg player = objectMapper.readValue(conn.getInputStream(),PlayerPubg.class);
				String id =  player.getData().get(0).getId();
				List<String> idmatches = new ArrayList<String>();
				for(int i=0;i<9;i++) { 
					idmatches.add(player.getData().get(0).getRelationships().getMatches().getData().get(i).getId());
				}
				
				List<Matchpubg> lista = new ArrayList<Matchpubg>();
				for(int j=0;j<idmatches.size();j++) {
					ObjectMapper objectMapper1 = new ObjectMapper();
					objectMapper1.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
					URL url1 = new URL("https://api.pubg.com/shards/steam/matches/"+idmatches.get(j));
					HttpURLConnection conn1 = (HttpURLConnection) url1.openConnection();
					conn1.setRequestMethod("GET");
					conn1.setRequestProperty("Accept", "application/vnd.api+json");
					pubgmatch.PubgMatch match = objectMapper1.readValue(conn1.getInputStream(),pubgmatch.PubgMatch.class);
					for(int i=0;i<match.getIncluded().size();i++) {
						if(match.getIncluded().get(i).getType().equals("participant")) {
							if(match.getIncluded().get(i).getAttributes().getStats().getName().equals(request.getParameter("name")	)) {
							Stats stats = match.getIncluded().get(i).getAttributes().getStats();
							Matchpubg jugador = new Matchpubg(match.getData().getAttributes().getMapName(),stats.getName(), stats.getKills(), stats.getWinPlace(),match.getData().getAttributes().getGameMode());
							lista.add(jugador);
							
						}
						}
						

					}
				}
				ObjectMapper objectMapper2 = new ObjectMapper();
				objectMapper2.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				String season="";
				String seasonJSP="";
				if(request.getParameter("season")==null) {
					season="division.bro.official.pc-2018-06";
					seasonJSP="SS6";
				}else {
					if(request.getParameter("season").equals("SS1")) {
						season="division.bro.official.pc-2018-01";
						seasonJSP="SS1";
					}else if(request.getParameter("season").equals("SS2")) {
						season="division.bro.official.pc-2018-02";
						seasonJSP="SS2";
					}else if(request.getParameter("season").equals("SS3")) {
						season="division.bro.official.pc-2018-03";
						seasonJSP="SS3";
					}else if(request.getParameter("season").equals("SS4")) {
						season="division.bro.official.pc-2018-04";
						seasonJSP="SS4";
					}else if(request.getParameter("season").equals("SS5")) {
						season="division.bro.official.pc-2018-05";
						seasonJSP="SS5";
					}else {
						season="division.bro.official.pc-2018-06";
						seasonJSP="SS6";
					}
				}
				
				URL url2 = new URL("https://api.pubg.com/shards/steam/players/"+id+"/seasons/" + season);
				HttpURLConnection conn2 = (HttpURLConnection) url2.openConnection();
				conn2.setRequestMethod("GET");
				conn2.setRequestProperty("Authorization","Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJiMDNhZWE2MC0zNmNlLTAxMzgtYmJjOS0zNzRkM2UxZGEzNjYiLCJpc3MiOiJnYW1lbG9ja2VyIiwiaWF0IjoxNTgyMjg2MDA0LCJwdWIiOiJibHVlaG9sZSIsInRpdGxlIjoicHViZyIsImFwcCI6InNlcmdpb3JvamFzamltIn0.dFS0GuKAPpTrOEChROMqc3APivDw-NDbwAhDpK4WMT8");
				conn2.setRequestProperty("Accept", "application/vnd.api+json");
				PubgSeason seasonstats = objectMapper2.readValue(conn2.getInputStream(),PubgSeason.class);
				Double kds =0.0;
				Double kdd = 0.0;
				Double kdsq = 0.0;
				Double kdsf = 0.0;
				Double kddf = 0.0;
				Double kdsqf = 0.0;
				String modoJSP="";
				if(request.getParameter("modo")==null) {
					kds=(double) (seasonstats.getData().getAttributes().getGameModeStats().getSolo().getKills().doubleValue()/seasonstats.getData().getAttributes().getGameModeStats().getSolo().getLosses().doubleValue());
					
					kdd=(double) (seasonstats.getData().getAttributes().getGameModeStats().getDuo().getKills().doubleValue()/seasonstats.getData().getAttributes().getGameModeStats().getDuo().getLosses().doubleValue());
				
					kdsq=(double) (seasonstats.getData().getAttributes().getGameModeStats().getSquad().getKills().doubleValue()/seasonstats.getData().getAttributes().getGameModeStats().getSquad().getLosses().doubleValue());
					modoJSP = "tpp";
				}else {
					if(request.getParameter("modo").equals("tpp")){
						kds=(double) (seasonstats.getData().getAttributes().getGameModeStats().getSolo().getKills().doubleValue()/seasonstats.getData().getAttributes().getGameModeStats().getSolo().getLosses().doubleValue());
						
						kdd=(double) (seasonstats.getData().getAttributes().getGameModeStats().getDuo().getKills().doubleValue()/seasonstats.getData().getAttributes().getGameModeStats().getDuo().getLosses().doubleValue());
					
						kdsq=(double) (seasonstats.getData().getAttributes().getGameModeStats().getSquad().getKills().doubleValue()/seasonstats.getData().getAttributes().getGameModeStats().getSquad().getLosses().doubleValue());
						modoJSP = "tpp";

					}else if(request.getParameter("modo").equals("fpp")){
						kdsf=(double) (seasonstats.getData().getAttributes().getGameModeStats().getSoloFpp().getKills().doubleValue()/seasonstats.getData().getAttributes().getGameModeStats().getSoloFpp().getLosses().doubleValue());
						
						kddf=(double) (seasonstats.getData().getAttributes().getGameModeStats().getDuoFpp().getKills().doubleValue()/seasonstats.getData().getAttributes().getGameModeStats().getDuoFpp().getLosses().doubleValue());
					
						kdsqf=(double) (seasonstats.getData().getAttributes().getGameModeStats().getSquadFpp().getKills().doubleValue()/seasonstats.getData().getAttributes().getGameModeStats().getSquadFpp().getLosses().doubleValue());
						modoJSP = "fpp";
					}else {
					}
				}
				DecimalFormat df2 = new DecimalFormat("#.##");
				request.setAttribute("modoJSP", modoJSP);
				request.setAttribute("seasonJSP", seasonJSP);
				request.setAttribute("kds", df2.format(kds));
				request.setAttribute("kdd", df2.format(kdd));
				request.setAttribute("kdsq", df2.format(kdsq));
				request.setAttribute("kdsf", df2.format(kdsf));
				request.setAttribute("kddf", df2.format(kddf));
				request.setAttribute("kdsqf", df2.format(kdsqf));

				request.setAttribute("lista", lista);
				request.getRequestDispatcher("/pruebapubg.jsp").forward(request, response);


			} catch (Exception e) {
				e.printStackTrace();
			}
			
			

			
		 
		

	}

	
	
	@SuppressWarnings("deprecation")
	public void doTrustToCertificates() throws Exception {
        Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkServerTrusted(X509Certificate[] certs, String authType) throws CertificateException {
                        return;
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType) throws CertificateException {
                        return;
                    }
                }
        };

        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        HostnameVerifier hv = new HostnameVerifier() {
            public boolean verify(String urlHostName, SSLSession session) {
                if (!urlHostName.equalsIgnoreCase(session.getPeerHost())) {
                    System.out.println("Warning: URL host '" + urlHostName + "' is different to SSLSession host '" + session.getPeerHost() + "'.");
                }
                return true;
            }
        };
        HttpsURLConnection.setDefaultHostnameVerifier(hv);
    }
	 @SuppressWarnings("unchecked")
	  public void addHeader(ClientResource cr, String headerName, String headerValue) {
	    Series<Header> headers = (Series<Header>) cr.getRequest().getAttributes()
	        .get(HeaderConstants.ATTRIBUTE_HEADERS);

	    if (headers == null) {
	      headers = new Series<Header>(Header.class);
	     cr.getRequest().getAttributes().put(HeaderConstants.ATTRIBUTE_HEADERS, headers);
	    }
		headers.add(headerName, headerValue);
	}
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
