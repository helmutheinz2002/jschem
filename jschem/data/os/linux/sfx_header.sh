#!/bin/sh
getAbsPath()
{
        cmd="$0"
        thisDir="`pwd`"
        absDir=`echo "$cmd" | awk '{
                if(substr($0, 1, 1) == "/")
                        print $0;
                else
                        print "'"$thisDir"'/"$0;
        }'`
        echo "`dirname "$absDir"`"
}

# Constants
APPNAME=JSchem
MAINCLASS=org.heinz.eda.schem.JSchem
MAINJAR=$APPNAME.jar
TRANSLATION_ZIP=translation.zip

# find absolute path
ABSPATH="`getAbsPath`"
BASENAME="`basename "$0"`"
ABSEXE="$ABSPATH"/"$BASENAME"
EXTRACT_ARCHIVE="$APPNAME.tgz"

# create a temp directory to extract to.
PREV="`pwd`"
WRKDIR="/tmp/$APPNAME$$"
mkdir "$WRKDIR"
cd "$WRKDIR"

echo Extracting ...

SKIP=`awk '/^__ARCHIVE_FOLLOWS__/ { print NR + 1; exit 0; }' "$ABSEXE"`

# Take the TGZ portion of this file and pipe it to tar.
tail -n +$SKIP "$ABSEXE" 2> /dev/null > "$WRKDIR"/"$EXTRACT_ARCHIVE"
TAILRC="$?"
if [ "$TAILRC" != "0" ] ; then
	# Non-GNU tail
	tail +$SKIP "$ABSEXE" 2> /dev/null > "$WRKDIR"/"$EXTRACT_ARCHIVE"
fi
cat "$WRKDIR"/"$EXTRACT_ARCHIVE" | gzip -d | tar xf -

# Look for 3D accelerated server
ENABLE_OPENGL="false"

# disabled for now
#HAS_GLXINFO=`which glxinfo`
if [ "$HAS_GLXINFO" != "" ] ; then
	HAS_OPENGL=`glxinfo | grep direct | awk '{print $3}'`
	if [ "$HAS_OPENGL" = "Yes" ] ; then
		ENABLE_OPENGL="True"
	fi
fi

echo Starting $APPNAME ...

# execute program
cd "$PREV"
java -Dsun.java2d.opengl="$ENABLE_OPENGL" -Xmx256m -classpath "$ABSPATH"/$TRANSLATION_ZIP:"$WRKDIR"/$MAINJAR $MAINCLASS "$@"

# delete the temp files
rm -rf "$WRKDIR"

exit 0

__ARCHIVE_FOLLOWS__
