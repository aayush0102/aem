package CrxProj.CrxProj;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.sql.DataSource;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.References;
import org.apache.felix.scr.annotations.Service;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.*;

import com.day.commons.datasource.poolservice.DataSourceNotFoundException;
import com.day.commons.datasource.poolservice.DataSourcePool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;


@SlingServlet(paths="/bin/myServlet", methods = "POST", metatype=true)

@Properties({
	    @Property(name="service.pid", value="CrxProj.CrxProj.CrxProjServlet",propertyPrivate=false),
	    @Property(name="service.description",value="This servlet writes to CRX Repository", propertyPrivate=false),
	    @Property(name="service.vendor",value="Adobe Systems Incorporated - Adobe@Adobe Team", propertyPrivate=false)
	})



public class CrxProjServlet extends SlingAllMethodsServlet {
	
	//there has to be separate refernce for using each service reference
	
	@Reference
	private DataSourcePool dsp;
	
	@Reference
	private ResourceResolverFactory resolverFactory;
	
	//getting the logger to output something in Logs.
	
	private final Logger log = LoggerFactory.getLogger(CrxProjServlet.class);
	
	
	protected void doPost(SlingHttpServletRequest request,
			 SlingHttpServletResponse response)
			throws ServletException, IOException 
	{
			
		//getting from JSON 
		
		String fname = request.getParameter("firstName");
		String lname = request.getParameter("lastName");
		PrintWriter out=response.getWriter();
		out.print("First Servlet");
		log.info("**********Inside Servlet doPost()**********");
		log.info("firstname  "+fname);
		log.info("lastname  "+lname);
		
					
		
		// Establishing DataBase Connection
		
		DataSource ds=null;
		Connection con=null;
		Statement st=null;
		 
		try
		{
			
			ds=(DataSource)dsp.getDataSource("aayushDSN");
			con= ds.getConnection();
			log.info(con.toString());
			st=con.createStatement();
			log.info(st.toString());
			String sql="insert into aayush.employee values('"+fname+" "+lname+"')";
			Boolean b= st.execute(sql);
			
			log.info(b.toString());
		}
		
		catch(Exception e){
			e.printStackTrace();
		}
		finally {
			 
		    try {
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 
		  	 
		}
			
			
		// Committing this in CRX repository
		
		String child=fname;
		Session session=null;
		
		try {
			
			
			ResourceResolver resolver = null;
			resolver = resolverFactory.getAdministrativeResourceResolver(null);
			
			/*
			 // following approach may be used in case we dont want to use deprecated method.
			  
			Map<String, Object> param = new HashMap<String, Object>();
			param.put(ResourceResolverFactory.SUBSERVICE, "datawrite");
			resolver = resolverFactory.getServiceResourceResolver(param); 
			
			*/
			
		    session = resolver.adaptTo(Session.class);
			log.info(session.toString());

			//	Node root=session.getRootNode();
			Node root=session.getNode("/aayush");
				root.addNode(child);
				session.save();
				log.info("nodes updated");
				
		} catch (LoginException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			
		
			session.logout();
		
		}
       
		
		 							
		}
	}
	


