IrcAppender

INSTALLATION

Add the jar to your classpath. 




USAGE (The Quick Version)

In your application, you need to add the following to your log4j properties file.


log4j.appender.irc=net.sf.ircappender.IrcAppender
log4j.appender.irc.host=irchostname.com
log4j.appender.irc.port=6667
log4j.appender.irc.username=log4jchat
log4j.appender.irc.password=password if needed
log4j.appender.irc.channel=#log4jchatchannel
log4j.appender.irc.buffersize=1000
log4j.appender.irc.buffertype=autopop
log4j.appender.irc.nickname=logtester
log4j.appender.irc.Threshold=ERROR

buffersize:  This is the number of logging entries to hold at any given time.  





KNOWN ISSUES:

IRC Communications is very slow.  If you attempt to send too many messages too 
fast, the server will disconnect you.  This is bad.  Since it is so slow you 
can get into a problem with losing entries off the front of the queue.  
Basically if your queue size is too small, and your application is generating 
them faster than you can pull them off onto the IRC server you will lose them 
as they are automatically popped off the front of the queue.

Currently, the appender is set to completely flush the queue and buffer before 
closing.  This is to insure you do not lose any information as the application
is closing down.  The problem lies with the fact that until the buffer is 
completely empty the application will not close.  Not sure what to do about 
this yet.

512 byte limit on a message.  A moderately long log message will be really truncated.



CONTACT:

Website: http://sourceforge.net/projects/ircappender

Download: http://sourceforge.net/projects/ircappender/files

Bug-Reports and Feature Requests:
http://sourceforge.net/tracker/?group_id=76057&atid=545921



CREDITS:

* Paul Mutton - Creator of PIRCBOT upon which all direct IRC interaction 
in this program is handled.  The PIRCBOT is freely available at
http://www.jibble.org/pircbot.php

