IRC Log4j appender.
============


Cloned from https://sourceforge.net/projects/ircappender/

Their readme: http://ircappender.sourceforge.net/

Mavenized and artifact put here: http://ondrazizka.googlecode.com/svn/maven/net/sf/ircappender/IrcAppender/


Usage
======

    log4j.rootLogger=DEBUG, irc, ...
    log4j.appender.irc=net.sf.ircappender.IrcAppender
    log4j.appender.irc.host=irchostname.com
    log4j.appender.irc.port=6667
    log4j.appender.irc.ssl=false
    log4j.appender.irc.username=log4jchat
    log4j.appender.irc.password=password if needed
    log4j.appender.irc.channel=#log4jchatchannel
    log4j.appender.irc.buffersize=1000
    log4j.appender.irc.buffertype=autopop
    log4j.appender.irc.nickname=logtester
    log4j.appender.irc.Threshold=ERROR
