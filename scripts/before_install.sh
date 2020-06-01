#! /bin/sh

target=java

pid=`ps aux | grep -v grep | grep "$target" | tr -s ' ' | cut -d ' ' -f 2`
if [ -n "$pid" ]; then
#  echo $pid
   sudo kill -9 $pid
fi
exit 0