package net.sourceforge.fullsync;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Hashtable;

import net.sourceforge.fullsync.fs.FileSystem;
import net.sourceforge.fullsync.fs.Site;
import net.sourceforge.fullsync.fs.buffering.BufferingProvider;
import net.sourceforge.fullsync.fs.buffering.syncfiles.SyncFilesBufferingProvider;
import net.sourceforge.fullsync.fs.filesystems.CommonsVfsFileSystem;
import net.sourceforge.fullsync.fs.filesystems.LocalFileSystem;
import net.sourceforge.fullsync.fs.filesystems.SftpFileSystem;
import net.sourceforge.fullsync.fs.filesystems.SmbFileSystem;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class FileSystemManager
{
    private Hashtable schemes;
    private Hashtable buffering;
    
    public FileSystemManager()
    {
        schemes = new Hashtable();
        schemes.put( "file", new LocalFileSystem() );
        schemes.put( "ftp", new CommonsVfsFileSystem() );
        schemes.put( "sftp", new CommonsVfsFileSystem() );
        schemes.put( "smb", new SmbFileSystem() );
        
        buffering = new Hashtable();
        buffering.put( "syncfiles", new SyncFilesBufferingProvider() );
    }
    
    public Site createConnection( ConnectionDescription desc )
    	throws FileSystemException, IOException, URISyntaxException
    {
        FileSystem fs = null;
        
        URI url = new URI( desc.getUri() );
        String scheme = url.getScheme();
        
        if( scheme != null )
        {
            fs = (FileSystem)schemes.get( scheme );
        }
        if( fs == null )
        {
            // TODO maybe we should test and correct this in profile dialog !?
            // no fs found, test for native path
            File f = new File( url.toString() ); // ignore query as local won't need query
            if( f.exists() )
            {
                fs = (FileSystem)schemes.get( "file" );
                url = f.toURI();
                desc.setUri( url.toString() );
            } else {
                throw new URISyntaxException( url.toString(), "Not a valid uri or unknown scheme" );
            }
        }
        
        Site s = fs.createConnection( desc );
        
        if( desc.getBufferStrategy() != null && !desc.getBufferStrategy().equals( "" ) )
            s = resolveBuffering( s, desc.getBufferStrategy() );
        
        return s;
    }
    
    public Site resolveBuffering( Site dir, String bufferStrategy )
		throws FileSystemException, IOException
    {
        BufferingProvider p = (BufferingProvider)buffering.get( bufferStrategy );
        
        if( p == null )
            throw new FileSystemException( "BufferStrategy '"+bufferStrategy+"' not found");
        
        return p.createBufferedSite( dir );
    }
}

