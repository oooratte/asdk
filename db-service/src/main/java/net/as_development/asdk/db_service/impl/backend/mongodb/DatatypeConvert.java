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
package net.as_development.asdk.db_service.impl.backend.mongodb;

import java.util.Date;

//==============================================================================
/**
 * @todo document me
 */
public class DatatypeConvert
{
    //--------------------------------------------------------------------------
    /** create new instance.
     */
    public DatatypeConvert ()
    {}

    //--------------------------------------------------------------------------
	public static String toString (Class< ? >  aType    ,
                                   Object      aValue   ,
                                   boolean     bScramble)
        throws Exception
    {
        if (aValue == null)
            return null;

        if (aType.equals(Date.class))
            return String.valueOf(((Date)aValue).getTime());
            
        return aValue.toString ();
/*
        if (aType.equals(String.class))
            return (String)aValue;
        else
        //if (aType.equals(int.class))
            return aValue.toString ();
        //else
        //  throw new RuntimeException ("Not implemented yet. Support more data types for SimpleDB please.");
 */
    }

    //--------------------------------------------------------------------------
	public static Object fromString (Class< ? > aType    ,
                                     String     sValue   ,
                                     boolean    bScramble)
        throws Exception
    {
        if (sValue == null)
            return null;

        Object aValue = null;
        
        if (aType.equals(String.class))
            aValue = sValue;
        else
        if (
            (aType.equals(char.class))
           )
            aValue = sValue.charAt(0);
        else
        if (
            (aType.equals(byte.class)) ||
            (aType.equals(Byte.class))
           )
            aValue = Byte.parseByte(sValue);
        else
        if (
            (aType.equals(short.class)) ||
            (aType.equals(Short.class))
           )
            aValue = Short.parseShort(sValue);
        else
        if (
            (aType.equals(int.class    )) ||
            (aType.equals(Integer.class))
           )
            aValue = Integer.parseInt(sValue);
        else
        if (
            (aType.equals(long.class)) ||
            (aType.equals(Long.class))
           )
            aValue = Long.parseLong(sValue);
        else
        if (
            (aType.equals(boolean.class)) ||
            (aType.equals(Boolean.class))
           )
            aValue = Boolean.parseBoolean(sValue);
        else
        if (
            (aType.equals(float.class)) ||
            (aType.equals(Float.class))
           )
            aValue = Float.parseFloat(sValue);
        else
        if (
            (aType.equals(double.class)) ||
            (aType.equals(Double.class))
           )
            aValue = Double.parseDouble(sValue);
        else
        if (aType.equals(Date.class))
            aValue = new Date(Long.parseLong(sValue));
        else
            throw new RuntimeException ("Not implemented yet. Support data type '"+aType.getName ()+"' for MongoDB please.");
        
        return aValue;
    }
}
