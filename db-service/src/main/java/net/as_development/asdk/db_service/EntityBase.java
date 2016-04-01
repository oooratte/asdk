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
package net.as_development.asdk.db_service;

import java.lang.reflect.Field;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import net.as_development.asdk.api.db.IEntity;
import net.as_development.asdk.api.db.PersistentAttribute;
import net.as_development.asdk.api.db.PersistentId;

//==============================================================================
/** provides basic functionality for all DB entities.
 *  Can be used as base class for those entities impl classes.
 */
@SuppressWarnings("serial")
public class EntityBase implements IEntity
{
    //--------------------------------------------------------------------------
    /// The API name for the ID attribute.
    public static final String ATTRIBUTE_NAME_ID = "id";

    //--------------------------------------------------------------------------
    /// The API name for the EXPIRE attribute.
    public static final String ATTRIBUTE_NAME_EXPIRE = "expire";

    //--------------------------------------------------------------------------
    /// The API name for the MODIFY_STAMP attribute.
    public static final String ATTRIBUTE_NAME_MODIFY_STAMP = "modify_stamp";

    //--------------------------------------------------------------------------
    /// The API name for the REMOVED attribute.
    public static final String ATTRIBUTE_NAME_REMOVED = "removed";
    
    //--------------------------------------------------------------------------
    /// The column name for the ID attribute.
    public static final String COLUMN_NAME_ID = EntityBase.ATTRIBUTE_NAME_ID;

    //--------------------------------------------------------------------------
    /// The column name for the EXPIRE attribute.
    public static final String COLUMN_NAME_EXPIRE = EntityBase.ATTRIBUTE_NAME_EXPIRE;
    
    //--------------------------------------------------------------------------
    /// The column name for the MODIFY_STAMP attribute.
    public static final String COLUMN_NAME_MODIFY_STAMP = EntityBase.ATTRIBUTE_NAME_MODIFY_STAMP;
    
    //--------------------------------------------------------------------------
    /// The column name for the REMOVED attribute.
    public static final String COLUMN_NAME_REMOVED = EntityBase.ATTRIBUTE_NAME_REMOVED;
    
    //--------------------------------------------------------------------------
    /** create new instance.
     */
    public EntityBase ()
    {}

    //--------------------------------------------------------------------------
    /** @return if these entity is already persistent within the DB back end or not.
     */
    public boolean isPersistent ()
		throws Exception
	{
		return ( ! StringUtils.isEmpty(Id));
	}

    //--------------------------------------------------------------------------
	/** mark the entity as persistent (or not persistent any longer) within the DB back end.
	 *
	 *  Because those state is known by the DB back end only those method
	 *  has not to be called from outside and anybody. Let it protected .-)
	 *
	 *  @param	bPersistent [IN]
	 *  		the new persistent state.
	 */
	public void setPersistent (boolean bPersistent)
		throws Exception
	{
		if (bPersistent == true)
		{
			// a) already persistent
			if ( ! StringUtils.isEmpty(Id))
				return;
			
			// b) an external ID overrule our own ID generation process.
			if ( ! StringUtils.isEmpty(ExternalId))
			{
				Id = ExternalId;
				return;
			}
			
			// c) at least use our own generated ID
			if (StringUtils.isEmpty(PreId))
				throw new IllegalArgumentException ("There is no ID (no external and nor pre generatged one) for this entity.");
			Id = PreId;
		}
		else
		{
			ExternalId = null;
			PreId      = null;
			Id         = null;
		}
	}

    //--------------------------------------------------------------------------
	/** @return the ID to be used for storing this entity.
	 * 
	 *  Because we know several ways to get an ID the following order
	 *  will be defined:
	 *  
	 *  a) if an external ID exists it's preferred
	 *  b) if a new pre-generated ID exists it's used next
	 *  c) if an ID already exists (because the entity is already
	 *     stored) it's used last. 
	 */
	public String getIDForStore ()
		throws Exception
	{
		if ( ! StringUtils.isEmpty(ExternalId))
			return ExternalId;

		if ( ! StringUtils.isEmpty(PreId))
			return PreId;
		
		return Id;
	}

    //--------------------------------------------------------------------------
	/** enable the expire feature for this entity and set the expire time in seconds from now.
	 *
	 *  @param	nExpire [IN]
	 *  		the time for expire in seconds.
	 */
	@Override
	public void setExpireInSeconds (long nExpire)
		throws Exception
	{
		impl_setExpire(nExpire * 1000);
	}

    //--------------------------------------------------------------------------
	/** enable the expire feature for this entity and set the expire time in minutes from now.
	 *
	 *  @param	nExpire [IN]
	 *  		the time for expire in minutes.
	 */
	@Override
	public void setExpireInMinutes (long nExpire)
		throws Exception
	{
		setExpireInSeconds (nExpire * 60);
	}

    //--------------------------------------------------------------------------
	/** @return true if entity is expired; false otherwise.
	 */
	public boolean isExpired ()
		throws Exception
	{
		if (Expire == null)
			return false;

		// Workaround !
		// Some back ends don't handle 'null' for atomic types like e.g. int or long.
		// Instead they set those values to default values like '0'.
		// So we have to handle those values explicit here .-)
		if (Expire < 1)
			return false;

		long nNow    = System.currentTimeMillis();
		long nExpire = Expire.longValue();

		return (nExpire < nNow);
	}

    //--------------------------------------------------------------------------
	/** has to be called by the DB layer in case this entity is created new or updated
	 *  within the DB back end. It has to be called BEFORE such write operation will be
	 *  done. We renew our modify-date-time-stamp then ...
	 *  it will be written to the DB and our backup solution can work with that value.
	 */
	public void setModifyStamp ()
		throws Exception
	{
		ModifyStamp = new Date ();
	}

	//--------------------------------------------------------------------------
	public Date getModifyStamp ()
		throws Exception
	{
		return ModifyStamp;
	}
	
	//--------------------------------------------------------------------------
	public void setRemoved (boolean bState)
		throws Exception
	{
		Removed = bState;
	}
	
	//--------------------------------------------------------------------------
	public boolean isRemoved ()
		throws Exception
	{
		return Removed;
	}

    //--------------------------------------------------------------------------
    /* dont override it ! makes different objects "same" in memory ?!
    @Override
    public int hashCode()
    {
        int nHash = 0;
        if (Id != null)
            nHash = Id.hashCode();
        return nHash;
    }
    */

    //--------------------------------------------------------------------------
    /* dont override it ! makes different objects "same" in memory ?!
    @Override
    public boolean equals(Object aObject)
    {
        if ( ! (aObject instanceof EntityBase))
            return false;

        EntityBase aOther = (EntityBase) aObject;
        return StringUtils.equals(Id, aOther.Id);
    }
    */

	//--------------------------------------------------------------------------
	/** TODO implement me */
    @Override
	public Object clone()
    	throws CloneNotSupportedException
	{
		return super.clone();
	}

	//--------------------------------------------------------------------------
	@Override
    public String toString()
    {
        StringBuffer sString = new StringBuffer (256);

        sString.append (super.toString ()+"\n");

        try
        {
            Class< ? > aClass = getClass ();
            impl_toString (aClass, sString);
        }
        catch (Throwable ex)
        {}

        return sString.toString ();
    }

    //--------------------------------------------------------------------------
	private void impl_toString (Class< ? >   aClass ,
                                StringBuffer sString)
        throws Exception
    {
        if (aClass == null)
            return;

        Field[] lFields = aClass.getDeclaredFields();
        for (Field aField : lFields)
        {
            try
            {
                aField.setAccessible(true);

                String sName  = aField.getName();
                Object aValue = aField.get(this);

                sString.append (sName+" = '"+aValue+"'\n");
            }
            catch (Throwable ex)
            {}
        }

        impl_toString (aClass.getSuperclass(), sString);
    }

    //--------------------------------------------------------------------------
	/** set the expire time for this entity in the future calculating those time from
	 *  now by adding the expire value as offset.
	 *
	 *  @param	nExpireOffset [IN]
	 *  		the expire offset in [ms]
	 */
	private void impl_setExpire (long nExpireOffset)
		throws Exception
	{
		// reset expire feature if called with '0' or more stupid values .-)
		if (nExpireOffset < 1)
		{
			Expire = null;
			return;
		}

		long nNow    = System.currentTimeMillis();
		long nExpire = nNow + nExpireOffset;
		     Expire  = nExpire;
	}
	
    //--------------------------------------------------------------------------
	/** help the DB back end to track the process of generating IDs within
	 *  error prone transactions. Has not to be used/called from 'outside'.
	 *  
	 *  BEFORE a new entity is stored such PreId is calculated and used
	 *  after storing the entity was successfully.
	 *
	 *  Further it has not to be made persistent - it's a transient property.
	 */
	public transient String PreId = null;

    //--------------------------------------------------------------------------
	/** Sometimes the outside code wish to use it's own ID instead of generating
	 *  a new one all the time. That's e.g. good for using the same ID in different
	 *  tables and speed up performance because one ID can be used within
	 *  several tables as key (where direct access will be possible instead
	 *  of using queries).
	 *  
	 *  Of course such ID has to match the ID-type of this entity.
	 *  
	 *  An existing external ID is preferred against PreId !
	 *
	 *  Further it has not to be made persistent - it's a transient property.
	 */
	public transient String ExternalId = null;
	
    //--------------------------------------------------------------------------
    /// The "primary key" of all entities derived from this class.
    @PersistentId(name    =EntityBase.ATTRIBUTE_NAME_ID ,
    			  column  =EntityBase.COLUMN_NAME_ID    ,
    			  strategy=PersistentId.EStrategy.E_UUID)
    public String Id = null;

    //--------------------------------------------------------------------------
    /** Special feature to let entities expire after the specified amount of time.
     *
     *  Define the time offset into the future where you wish that those entity
     *  should expire automatic.
     *
     *  Define nothing (or set it to 'null' explicit) if you don't wish to use that
     *  feature.
     *
     *  The time to be set here must be in [ms] ... or you use specialized helper methods
     *  on this class to set/get those value.
     */
    @PersistentAttribute(name         =EntityBase.ATTRIBUTE_NAME_EXPIRE,
    			  		 column       =EntityBase.COLUMN_NAME_EXPIRE   ,
    			  		 can_be_null  =true							   ,
    			  		 allow_updates=true							   )
    public Long Expire = null;

    //--------------------------------------------------------------------------
    /** Special feature to know when a DB entry was modified last time.
     *
     *  Its the time where the last write operation on that entry was done.
     *  Our backup solution can rely on that and e.g. backup all new items after
     *  a special date.
     */
    @PersistentAttribute(name         =EntityBase.ATTRIBUTE_NAME_MODIFY_STAMP,
    			  		 column       =EntityBase.COLUMN_NAME_MODIFY_STAMP   ,
    			  		 can_be_null  =false							     ,
    			  		 allow_updates=true							         )
    public Date ModifyStamp = null;

    //--------------------------------------------------------------------------
    /** Interims feature to know 'removed' items in DB ...
     *  If and how we use that information isn't clear at the moment .-)
     */
    @PersistentAttribute(name         =EntityBase.ATTRIBUTE_NAME_REMOVED,
    			  		 column       =EntityBase.COLUMN_NAME_REMOVED   ,
    			  		 can_be_null  =false							,
    			  		 allow_updates=true							    )
    public boolean Removed = false;
}
