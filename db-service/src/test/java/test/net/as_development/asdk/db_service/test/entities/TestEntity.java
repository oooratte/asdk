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
package test.net.as_development.asdk.db_service.test.entities;

import java.util.Date;

import net.as_development.asdk.api.db.PersistentAttribute;
import net.as_development.asdk.api.db.PersistentEntity;
import net.as_development.asdk.db_service.EntityBase;

import org.junit.Ignore;

//==============================================================================
/** An entity for unit test purposes.
 *  It should use ALL features supported by the DB service implementation.
 */
@Ignore
@PersistentEntity(name ="TestEntity"         ,
				  table=TestEntity.TABLE_NAME)
public class TestEntity extends EntityBase
{
    //-------------------------------------------------------------------------
    public static final String TABLE_NAME = "test_entity";
    
	//-------------------------------------------------------------------------
	private static final long serialVersionUID = 3331844825855873976L;

    //--------------------------------------------------------------------------
	public static final String ATTRIBUTE_NAME_STRINGVALUE        = "StringValue";
    public static final String ATTRIBUTE_NAME_CHARVALUE          = "CharValue";
    public static final String ATTRIBUTE_NAME_SIMPLEBOOLEANVALUE = "SimpleBooleanValue";
    public static final String ATTRIBUTE_NAME_BOOLEANVALUE       = "BooleanValue";
    public static final String ATTRIBUTE_NAME_SIMPLEBYTEVALUE    = "SimpleByteValue";
    public static final String ATTRIBUTE_NAME_BYTEVALUE          = "ByteValue";
    public static final String ATTRIBUTE_NAME_SIMPLESHORTVALUE   = "SimpleShortValue";
    public static final String ATTRIBUTE_NAME_SHORTVALUE         = "ShortValue";
    public static final String ATTRIBUTE_NAME_SIMPLEINTVALUE     = "SimpleIntValue";
    public static final String ATTRIBUTE_NAME_INTVALUE           = "IntValue";
    public static final String ATTRIBUTE_NAME_SIMPLELONGVALUE    = "SimpleLongValue";
    public static final String ATTRIBUTE_NAME_LONGVALUE          = "LongValue";
    public static final String ATTRIBUTE_NAME_SIMPLEDOUBLEVALUE  = "SimpleDoubleValue";
    public static final String ATTRIBUTE_NAME_DOUBLEVALUE        = "DoubleValue";
    public static final String ATTRIBUTE_NAME_SIMPLEFLOATVALUE   = "SimpleFloatValue";
    public static final String ATTRIBUTE_NAME_FLOATVALUE         = "FloatValue";
    public static final String ATTRIBUTE_NAME_DATEVALUE          = "DateValue";
    
    //--------------------------------------------------------------------------
    public static final String COLUMN_NAME_STRINGVALUE        = "string_value";
    public static final String COLUMN_NAME_CHARVALUE          = "char_value";
    public static final String COLUMN_NAME_SIMPLEBOOLEANVALUE = "simple_boolean_value";
    public static final String COLUMN_NAME_BOOLEANVALUE       = "boolean_value";
    public static final String COLUMN_NAME_SIMPLEBYTEVALUE    = "simple_byte_value";
    public static final String COLUMN_NAME_BYTEVALUE          = "byte_value";
    public static final String COLUMN_NAME_SIMPLESHORTVALUE   = "simple_short_value";
    public static final String COLUMN_NAME_SHORTVALUE         = "short_value";
    public static final String COLUMN_NAME_SIMPLEINTVALUE     = "simple_int_value";
    public static final String COLUMN_NAME_INTVALUE           = "int_value";
    public static final String COLUMN_NAME_SIMPLELONGVALUE    = "simple_long_value";
    public static final String COLUMN_NAME_LONGVALUE          = "long_value";
    public static final String COLUMN_NAME_SIMPLEDOUBLEVALUE  = "simple_double_value";
    public static final String COLUMN_NAME_DOUBLEVALUE        = "double_value";
    public static final String COLUMN_NAME_SIMPLEFLOATVALUE   = "simple_float_value";
    public static final String COLUMN_NAME_FLOATVALUE         = "float_value";
    public static final String COLUMN_NAME_DATEVALUE          = "date_value";
    
    //--------------------------------------------------------------------------
    public TestEntity ()
    {}

    //--------------------------------------------------------------------------
    @PersistentAttribute (name         =TestEntity.ATTRIBUTE_NAME_STRINGVALUE,
    					  column       =TestEntity.COLUMN_NAME_STRINGVALUE,
    					  can_be_null  =true,
    					  allow_updates=true,          
    					  length       =40)
    public String StringValue = null;

    //--------------------------------------------------------------------------
    @PersistentAttribute (name         =TestEntity.ATTRIBUTE_NAME_SIMPLEBOOLEANVALUE,
                          column       =TestEntity.COLUMN_NAME_SIMPLEBOOLEANVALUE,
			  			  can_be_null  =false                 ,
			  			  allow_updates=true                  )
    public boolean SimpleBooleanValue = false;

    //--------------------------------------------------------------------------
    @PersistentAttribute (name         =TestEntity.ATTRIBUTE_NAME_BOOLEANVALUE,
                          column       =TestEntity.COLUMN_NAME_BOOLEANVALUE,
			  			  can_be_null  =true           ,
			  			  allow_updates=true           )
    public Boolean BooleanValue = false;
    
    //--------------------------------------------------------------------------
    @PersistentAttribute (name         =TestEntity.ATTRIBUTE_NAME_SIMPLEBYTEVALUE,
                          column       =TestEntity.COLUMN_NAME_SIMPLEBYTEVALUE,
			  			  can_be_null  =false              ,
			  			  allow_updates=true               )
    public byte SimpleByteValue = 0;

    //--------------------------------------------------------------------------
    @PersistentAttribute (name         =TestEntity.ATTRIBUTE_NAME_BYTEVALUE,
                          column       =TestEntity.COLUMN_NAME_BYTEVALUE,
                          can_be_null  =true        ,
                          allow_updates=true        )
    public Byte ByteValue = 0;

    //--------------------------------------------------------------------------
    @PersistentAttribute (name         =TestEntity.ATTRIBUTE_NAME_CHARVALUE,
                          column       =TestEntity.COLUMN_NAME_CHARVALUE,
			  			  can_be_null  =false       ,
			  			  allow_updates=true        )
    public char CharValue = ' ';

    //--------------------------------------------------------------------------
    @PersistentAttribute (name         =TestEntity.ATTRIBUTE_NAME_SHORTVALUE,
                          column       =TestEntity.COLUMN_NAME_SHORTVALUE,
                          can_be_null  =true         ,
                          allow_updates=true         )
    public Short ShortValue = 0;
    
    //--------------------------------------------------------------------------
    @PersistentAttribute (name         =TestEntity.ATTRIBUTE_NAME_SIMPLESHORTVALUE,
                          column       =TestEntity.COLUMN_NAME_SIMPLESHORTVALUE,
			  			  can_be_null  =false               ,
			  			  allow_updates=true                )
    public short SimpleShortValue = 0;

    //--------------------------------------------------------------------------
    @PersistentAttribute (name         =TestEntity.ATTRIBUTE_NAME_INTVALUE,
                          column       =TestEntity.COLUMN_NAME_INTVALUE,
			  			  can_be_null  =true       ,
			  			  allow_updates=true       )
    public Integer IntValue = 0;

    //--------------------------------------------------------------------------
    @PersistentAttribute (name         =TestEntity.ATTRIBUTE_NAME_SIMPLEINTVALUE,
                          column       =TestEntity.COLUMN_NAME_SIMPLEINTVALUE,
                          can_be_null  =false             ,
                          allow_updates=true              )
    public int SimpleIntValue = 0;
    
    //--------------------------------------------------------------------------
    @PersistentAttribute (name         =TestEntity.ATTRIBUTE_NAME_LONGVALUE,
                          column       =TestEntity.COLUMN_NAME_LONGVALUE,
			  			  can_be_null  =false       ,
			  			  allow_updates=true        )
    public Long LongValue = 0L;
    
    //--------------------------------------------------------------------------
    @PersistentAttribute (name         =TestEntity.ATTRIBUTE_NAME_SIMPLELONGVALUE,
                          column       =TestEntity.COLUMN_NAME_SIMPLELONGVALUE,
                          can_be_null  =false              ,
                          allow_updates=true               )
    public long SimpleLongValue = 0L;

    //--------------------------------------------------------------------------
    @PersistentAttribute (name         =TestEntity.ATTRIBUTE_NAME_DOUBLEVALUE,
                          column       =TestEntity.COLUMN_NAME_DOUBLEVALUE,
                          can_be_null  =true          ,
                          allow_updates=true          )
    public Double DoubleValue = 0.0;
    
    //--------------------------------------------------------------------------
    @PersistentAttribute (name         =TestEntity.ATTRIBUTE_NAME_SIMPLEDOUBLEVALUE,
                          column       =TestEntity.COLUMN_NAME_SIMPLEDOUBLEVALUE,
			  			  can_be_null  =false                ,
			  			  allow_updates=true                 )
    public double SimpleDoubleValue = 0.0;

    //--------------------------------------------------------------------------
    @PersistentAttribute (name         =TestEntity.ATTRIBUTE_NAME_FLOATVALUE,
                          column       =TestEntity.COLUMN_NAME_FLOATVALUE,
			  			  can_be_null  =false        ,
			  			  allow_updates=true         )
    public Float FloatValue = 0.0f;

    //--------------------------------------------------------------------------
    @PersistentAttribute (name         =TestEntity.ATTRIBUTE_NAME_SIMPLEFLOATVALUE,
                          column       =TestEntity.COLUMN_NAME_SIMPLEFLOATVALUE,
                          can_be_null  =false               ,
                          allow_updates=true                )
    public float SimpleFloatValue = 0.0f;
    
    //--------------------------------------------------------------------------
    @PersistentAttribute (name         =TestEntity.ATTRIBUTE_NAME_DATEVALUE,
                          column       =TestEntity.COLUMN_NAME_DATEVALUE,
			  			  can_be_null  =true        ,
			  			  allow_updates=true        )
    public Date DateValue = null;
}
