#!/bin/bash
SELF_ABSOLUTE_PATH="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)/$(basename "${BASH_SOURCE[0]}")"
WORKING_DIR=$(dirname $SELF_ABSOLUTE_PATH)
read_vmoptions() {
  vmoptions_file=`eval echo "$1" 2>/dev/null`
  if [ ! -r "$vmoptions_file" ]; then
    vmoptions_file="$prg_dir/$vmoptions_file"
  fi
  if [ -r "$vmoptions_file" ] && [ -f "$vmoptions_file" ]; then
    exec 8< "$vmoptions_file"
    while read cur_option<&8; do
      is_comment=`expr "W$cur_option" : 'W *#.*'`
      if [ "$is_comment" = "0" ]; then
      	echo $(eval echo $cur_option)
        vmo_include=`expr "W$cur_option" : 'W *-include-options \(.*\)'`
        if [ "$vmo_include" = "" ]; then
          JVM_OPTS="$JVM_OPTS $(eval echo $cur_option)"
        fi
      fi
    done
    exec 8<&-
    if [ ! "$vmo_include" = "" ]; then
      read_vmoptions "$vmo_include"
    fi
  fi
}
JVM_OPTS=""
read_vmoptions $WORKING_DIR/historical-app.vmoptions
MAIN_CLASS=ai.caria.historical.app.HistoricalApplication
LOCAL_CLASSPATH="$WORKING_DIR/*:$WORKING_DIR/lib/*"
java $JVM_OPTS -cp "$LOCAL_CLASSPATH" -Dworking.dir=$WORKING_DIR $MAIN_CLASS $@