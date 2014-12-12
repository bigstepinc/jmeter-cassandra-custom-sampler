package com.bigstep;


import com.datastax.driver.core.*;
 

import java.io.Serializable;
import java.io.File;

import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;



import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

import java.net.URI;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
 
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
 
public class CasSampler extends AbstractJavaSamplerClient implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final org.apache.log.Logger log = LoggingManager.getLoggerForClass();
    private Cluster casCluster = null;
    private Session casSession = null;
    private  byte[] putContents = null;
 
    // set up default arguments for the JMeter GUI
    @Override
    public Arguments getDefaultParameters() {
        Arguments defaultParameters = new Arguments();
        defaultParameters.addArgument("cluster_contact_points", "127.0.0.1");
        defaultParameters.addArgument("schema", "default");
        defaultParameters.addArgument("query", "SELECT * FROM test");

        return defaultParameters;
    }
   	
    @Override 
    public void setupTest(JavaSamplerContext context)
    {
	String debug = context.getParameter( "debug" );
	
	try
	{
		Cluster.Builder cluster_builder = Cluster.builder(); 

		String cluster_contact_points = context.getParameter( "cluster_contact_points" );

		for(String contact_point : cluster_contact_points.split(","))
			cluster_builder.addContactPoint(contact_point);
		
		this.casCluster = cluster_builder.build();					
		
		String schema = context.getParameter( "schema" );

		this.casSession = this.casCluster.connect(schema);	

	}
	catch(Exception ex)
	{
            java.io.StringWriter stringWriter = new java.io.StringWriter();
            ex.printStackTrace( new java.io.PrintWriter( stringWriter ) );

	    log.error("setupTest:"+ex.getMessage()+stringWriter.toString());
 
	}
			
    }
    
    @Override	
    public void teardownTest(JavaSamplerContext context)
    {
	if(null!=this.casCluster)
		this.casCluster.close();		
    }
 
    @Override
    public SampleResult runTest(JavaSamplerContext context) {

        String query = context.getParameter( "query" );
        
	SampleResult result = new SampleResult();
        result.sampleStart(); // start stopwatch
         
        try {

		if( null==this.casCluster || null==this.casSession)
			throw new Exception("Cassandra client not initialised");

	    long startTime=System.nanoTime();	
		
	    ResultSet results = this.casSession.execute(query);
	    List<Row> rows = results.all();
	    //we do not do anything with the list yet
	    long endTime=System.nanoTime();
			
    
            result.sampleEnd(); // stop stopwatch
            result.setSuccessful( true );
            result.setResponseMessage( Long.toString(endTime-startTime) );
            result.setResponseCodeOK(); // 200 code

        } catch (Exception e) {
            result.sampleEnd(); // stop stopwatch
            result.setSuccessful( false );
            result.setResponseMessage( "Exception: " + e );
 
            // get stack trace as a String to return as document data
            java.io.StringWriter stringWriter = new java.io.StringWriter();
            e.printStackTrace( new java.io.PrintWriter( stringWriter ) );
            result.setResponseData( stringWriter.toString() );
            result.setDataType( org.apache.jmeter.samplers.SampleResult.TEXT );
            result.setResponseCode( "500" );

	     log.error("runTest:"+e.getMessage()+" "+stringWriter.toString());
        }
 
        return result;
    }
}

