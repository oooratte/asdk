/**
 * This is free and unencumbered software released into the public domain.
 *
 * Anyone is free to copy, modify, publish, use, compile, sell, or
 * distribute this software, either in source code form or as a compiled
 * binary, for any purpose, commercial or non-commercial, and by any
 * means.
 *
 * In jurisdictions that recognize copyright laws, the author or authors
 * of this software dedicate any and all copyright interest in the
 * software to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and
 * successors. We intend this dedication to be an overt act of
 * relinquishment in perpetuity of all present and future rights to this
 * software under copyright law.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 * For more information, please refer to <http://unlicense.org/>
 */
package net.as_development.asdk.db_service.impl;

import java.lang.reflect.Field;

import net.as_development.asdk.db_service.EntityBase;

//==============================================================================
/**
 * TODO document me
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
