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
package net.as_development.asdk.db_service.impl;

import java.lang.reflect.Field;

import net.as_development.asdk.db_service.EntityBase;

//==============================================================================
/**
 * @todo document me
 */
public class PersistentEntity
{
    //--------------------------------------------------------------------------
    /** create new instance.
     */
    public PersistentEntity ()
    {}

    //--------------------------------------------------------------------------
    public PersistentEntity (EntityMetaInfo aDescriptor,
                             EntityBase     aEntity    )
        throws Exception
    {
        setEntityMetaInfo (aDescriptor);
        setEntity         (aEntity    );
    }

    //--------------------------------------------------------------------------
    public void setEntityMetaInfo (EntityMetaInfo aMetaInfo)
        throws Exception
    {
        m_aMetaInfo = aMetaInfo;
    }

    //--------------------------------------------------------------------------
    public EntityMetaInfo getEntityMetaInfo ()
        throws Exception
    {
        return m_aMetaInfo;
    }

    //--------------------------------------------------------------------------
    public void setEntity (EntityBase aEntity)
        throws Exception
    {
        m_aEntity = aEntity;
    }

    //--------------------------------------------------------------------------
    public EntityBase getEntity ()
        throws Exception
    {
        return m_aEntity;
    }

    //--------------------------------------------------------------------------
    public void setAttributeValue (String sAttribute,
                                   Object aValue    )
        throws Exception
    {
        AttributeMetaInfo aAttribute = m_aMetaInfo.getAttributes().getForAttributeName(sAttribute);
        Field             aField     = aAttribute.getClassField();

        aField.setAccessible(true);
        aField.set(m_aEntity, aValue);
    }

    //--------------------------------------------------------------------------
    public Object getAttributeValue (String sAttribute)
        throws Exception
    {
        AttributeMetaInfo aAttribute = m_aMetaInfo.getAttributes().getForAttributeName(sAttribute);
        Field             aField     = aAttribute.getClassField();

        aField.setAccessible(true);
        return aField.get(m_aEntity);
    }

    //--------------------------------------------------------------------------
    private EntityMetaInfo m_aMetaInfo = null;

    //--------------------------------------------------------------------------
    private EntityBase m_aEntity = null;
}
