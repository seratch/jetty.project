//
//  ========================================================================
//  Copyright (c) 1995-2016 Mort Bay Consulting Pty. Ltd.
//  ------------------------------------------------------------------------
//  All rights reserved. This program and the accompanying materials
//  are made available under the terms of the Eclipse Public License v1.0
//  and Apache License v2.0 which accompanies this distribution.
//
//      The Eclipse Public License is available at
//      http://www.eclipse.org/legal/epl-v10.html
//
//      The Apache License v2.0 is available at
//      http://www.opensource.org/licenses/apache2.0.php
//
//  You may elect to redistribute this code under either of these licenses.
//  ========================================================================
//


package org.eclipse.jetty.server.session;


import org.eclipse.jetty.util.component.AbstractLifeCycle;

/**
 * AbstractSessionDataStore
 *
 *
 */
public abstract class AbstractSessionDataStore extends AbstractLifeCycle implements SessionDataStore
{
    protected SessionContext _context; //context associated with this session data store


    public abstract void doStore(String id, SessionData data, boolean isNew) throws Exception;

   

    
    public void initialize (SessionContext context)
    {
        if (isStarted())
            throw new IllegalStateException("Context set after SessionDataStore started");
        _context = context;
    }

    /** 
     * @see org.eclipse.jetty.server.session.SessionDataStore#store(java.lang.String, org.eclipse.jetty.server.session.SessionData)
     */
    @Override
    public void store(String id, SessionData data) throws Exception
    {
        long lastSave = data.getLastSaved();
        
        data.setLastSaved(System.currentTimeMillis());
        try
        {
            doStore(id, data, (lastSave<=0));
        }
        catch (Exception e)
        {
            //reset last save time
            data.setLastSaved(lastSave);
        }
        finally
        {
            data.setDirty(false);
        }
    }
    

    /** 
     * @see org.eclipse.jetty.server.session.SessionDataStore#newSessionData(org.eclipse.jetty.server.session.SessionKey, long, long, long, long)
     */
    @Override
    public SessionData newSessionData(String id, long created, long accessed, long lastAccessed, long maxInactiveMs)
    {
        return new SessionData(id, _context.getCanonicalContextPath(), _context.getVhost(), created, accessed, lastAccessed, maxInactiveMs);
    }
 
    protected void checkStarted () throws IllegalStateException
    {
        if (isStarted())
            throw new IllegalStateException("Already started");
    }




    @Override
    protected void doStart() throws Exception
    {
        if (_context == null)
            throw new IllegalStateException ("No SessionContext");
        
        super.doStart();
    }
    
    
    
}
