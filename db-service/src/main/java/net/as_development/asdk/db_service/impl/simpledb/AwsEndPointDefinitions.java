/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package net.as_development.asdk.db_service.impl.simpledb;

import org.apache.commons.lang3.StringUtils;

//==============================================================================
/**
 */
public class AwsEndPointDefinitions
{
    //--------------------------------------------------------------------------
	public enum ERegion
	{
		E_NORTHERN_VIRGINIA,
		E_NORTHERN_CALIFORNIA,
		E_IRELAND,
		E_SINGAPORE,
		E_JAPAN
	}

    //--------------------------------------------------------------------------
	public enum EService
	{
		E_EC2,
		E_S3,
		E_DB,
		E_EMAIL,
		E_NOTIFICATION,
		E_QUEUE,
		E_AUTOSCALING,
		E_CLOUDFORMATION,
		E_ELASTIC_BEANSTALK,
		E_ELASTIC_LOAD_BALANCING
	}

    //--------------------------------------------------------------------------
	public static final String REGIONNAME_NORTHERN_VIRGINIA   = "Northern-Virginia";
	public static final String REGIONNAME_NORTHERN_CALIFORNIA = "Northern-California";
	public static final String REGIONNAME_IRELAND             = "Ireland";
	public static final String REGIONNAME_SINGAPORE           = "Singapore";
	public static final String REGIONNAME_JAPAN               = "Japan";
	
    //--------------------------------------------------------------------------
	private static final int REGIONINDEX_NORTHERN_VIRGINIA   = 0;
	private static final int REGIONINDEX_NORTHERN_CALIFORNIA = 1;
	private static final int REGIONINDEX_IRELAND             = 2;
	private static final int REGIONINDEX_SINGAPORE           = 3;
	private static final int REGIONINDEX_JAPAN               = 4;
	
    //--------------------------------------------------------------------------
	private static final String[] ENDPOINTS_4_EC2 = new String[]
    {
		"ec2.us-east-1.amazonaws.com",
		"ec2.us-west-1.amazonaws.com",
		"ec2.eu-west-1.amazonaws.com",
		"ec2.ap-southeast-1.amazonaws.com",
		"ec2.ap-northeast-1.amazonaws.com"
    };
	
    //--------------------------------------------------------------------------
	private static final String[] ENDPOINTS_4_S3 = new String[]
    {
		"s3.amazonaws.com",	 
		"s3-us-west-1.amazonaws.com",
		"s3-eu-west-1.amazonaws.com",
		"s3-ap-southeast-1.amazonaws.com",
		"s3-ap-northeast-1.amazonaws.com"
	};
	
    //--------------------------------------------------------------------------
	private static final String[] ENDPOINTS_4_DB = new String[]
    {
		"sdb.amazonaws.com",
		"sdb.us-west-1.amazonaws.com",
		"sdb.eu-west-1.amazonaws.com",
		"sdb.ap-southeast-1.amazonaws.com",
		"sdb.ap-northeast-1.amazonaws.com"
	};
	
    //--------------------------------------------------------------------------
	public static String getEndPoint (String   sRegion ,
							          EService eService)
		throws Exception
	{
		int nIndex = AwsEndPointDefinitions.impl_mapRegionToIndex(sRegion);
		return AwsEndPointDefinitions.impl_getEndPoint(nIndex, eService);
	}
	
    //--------------------------------------------------------------------------
	public static String getEndPoint (ERegion  eRegion ,
							          EService eService)
		throws Exception
	{
		int nIndex = AwsEndPointDefinitions.impl_mapRegionToIndex(eRegion);
		return AwsEndPointDefinitions.impl_getEndPoint(nIndex, eService);
	}
	
    //--------------------------------------------------------------------------
	private static String impl_getEndPoint (int      nRegionIndex,
							                EService eService    )
		throws Exception
	{
		switch (eService)
		{
			case E_EC2						:	return AwsEndPointDefinitions.ENDPOINTS_4_EC2[nRegionIndex];
			case E_S3						:	return AwsEndPointDefinitions.ENDPOINTS_4_S3 [nRegionIndex];
			case E_DB						:	return AwsEndPointDefinitions.ENDPOINTS_4_DB [nRegionIndex];
			/*
			case E_EMAIL,
			case E_NOTIFICATION,
			case E_QUEUE,
			case E_AUTOSCALING,
			case E_CLOUDFORMATION,
			case E_ELASTIC_BEANSTALK,
			case E_ELASTIC_LOAD_BALANCING
			*/
			default : throw new UnsupportedOperationException ("Please add support for service '"+eService+"'.");
		}
	}
	
    //--------------------------------------------------------------------------
	private static int impl_mapRegionToIndex (String sRegion)
		throws Exception
	{
		if (StringUtils.equalsIgnoreCase(sRegion, AwsEndPointDefinitions.REGIONNAME_NORTHERN_VIRGINIA))
			return AwsEndPointDefinitions.REGIONINDEX_NORTHERN_VIRGINIA;
		
		if (StringUtils.equalsIgnoreCase(sRegion, AwsEndPointDefinitions.REGIONNAME_NORTHERN_CALIFORNIA))
			return AwsEndPointDefinitions.REGIONINDEX_NORTHERN_CALIFORNIA;
		
		if (StringUtils.equalsIgnoreCase(sRegion, AwsEndPointDefinitions.REGIONNAME_IRELAND))
			return AwsEndPointDefinitions.REGIONINDEX_IRELAND;
		
		if (StringUtils.equalsIgnoreCase(sRegion, AwsEndPointDefinitions.REGIONNAME_SINGAPORE))
			return AwsEndPointDefinitions.REGIONINDEX_SINGAPORE;
		
		if (StringUtils.equalsIgnoreCase(sRegion, AwsEndPointDefinitions.REGIONNAME_JAPAN))
			return AwsEndPointDefinitions.REGIONINDEX_JAPAN;
		
		// ?! .-)
		return AwsEndPointDefinitions.REGIONINDEX_IRELAND;
	}

    //--------------------------------------------------------------------------
	private static int impl_mapRegionToIndex (ERegion eRegion)
		throws Exception
	{
		switch (eRegion)
		{
			case E_NORTHERN_VIRGINIA	:	return AwsEndPointDefinitions.REGIONINDEX_NORTHERN_VIRGINIA;
			case E_NORTHERN_CALIFORNIA	:	return AwsEndPointDefinitions.REGIONINDEX_NORTHERN_CALIFORNIA;
			case E_IRELAND 				:	return AwsEndPointDefinitions.REGIONINDEX_IRELAND;
			case E_SINGAPORE			:	return AwsEndPointDefinitions.REGIONINDEX_SINGAPORE;
			case E_JAPAN				:	return AwsEndPointDefinitions.REGIONINDEX_JAPAN;
		}
		
		// ?! .-)
		return AwsEndPointDefinitions.REGIONINDEX_IRELAND;
	}
}
