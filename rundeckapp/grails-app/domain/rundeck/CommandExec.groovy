package rundeck

/*
 * Copyright 2010 DTO Labs, Inc. (http://dtolabs.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
* CommandExec.java
*
* User: Greg Schueler <a href="mailto:greg@dtosolutions.com">greg@dtosolutions.com</a>
* Created: Feb 25, 2010 3:02:01 PM
* $Id$
*/

public class CommandExec extends WorkflowStep  {
    String argString
    String adhocRemoteString
    String adhocLocalString
    String adhocFilepath
    Boolean adhocExecution = false
    static transients = ['nodeStep']

    static mapping = {
        adhocLocalString type: 'text'
        adhocRemoteString type: 'text'
        adhocFilepath type: 'text'
        argString type: 'text'
    }
    public String toString() {
        StringBuffer sb = new StringBuffer()
        sb << "command( "
        sb << (adhocRemoteString ? "exec: ${adhocRemoteString}" : '')
        sb << (adhocLocalString ? "script: ${adhocLocalString}" : '')
        sb << (adhocFilepath ? "scriptfile: ${adhocFilepath}" : '')
        sb << (argString ? "scriptargs: ${argString}" : '')
        sb << (errorHandler ? " [handler: ${errorHandler}]" : '')
        sb << (null!= keepgoingOnSuccess ? " keepgoingOnSuccess: ${keepgoingOnSuccess}" : '')
        sb<<")"

        return sb.toString()
    }

    public String summarize(){
        StringBuffer sb = new StringBuffer()
        sb << (adhocRemoteString ? "${adhocRemoteString}" : '')
        sb << (adhocLocalString ? "${adhocLocalString}" : '')
        sb << (adhocFilepath ? "${adhocFilepath}" : '')
        sb << (argString ? " -- ${argString}" : '')
        return sb.toString()
    }

    static constraints = {
        argString(nullable: true)
        adhocRemoteString(nullable:true)
        adhocLocalString(nullable:true)
        adhocFilepath(nullable:true)
        errorHandler(nullable: true)
        keepgoingOnSuccess(nullable: true)
    }

    public CommandExec createClone(){
        Map properties = new HashMap(this.properties)
        properties.remove('errorHandler')
        CommandExec ce = new CommandExec(properties)
        return ce
    }



    /**
    * Return canonical map representation
     */
    public Map toMap(){
        def map=[:]
        if(adhocRemoteString){
            map.exec=adhocRemoteString
        }else if(adhocLocalString){
            map.script=adhocLocalString
        }else {
            if(adhocFilepath==~/^(?i:https?|file):.*$/){
                map.scripturl = adhocFilepath
            }else{
                map.scriptfile=adhocFilepath
            }
        }
        if(argString && !adhocRemoteString){
            map.args=argString
        }
        if(errorHandler){
            map.errorhandler=errorHandler.toMap()
        }else if(keepgoingOnSuccess){
            map.keepgoingOnSuccess= keepgoingOnSuccess
        }
        return map
    }

    static CommandExec fromMap(Map data){
        CommandExec ce = new CommandExec()
        if(data.exec){
            ce.adhocExecution = true
            ce.adhocRemoteString=data.exec
        }else if(data.script){
            ce.adhocExecution = true
            ce.adhocLocalString=data.script
        }else if(data.scriptfile){
            ce.adhocExecution = true
            ce.adhocFilepath=data.scriptfile
        }else if(data.scripturl){
            ce.adhocExecution = true
            ce.adhocFilepath=data.scripturl
        }
        if(data.args && !ce.adhocRemoteString){
            ce.argString=data.args
        }
        ce.keepgoingOnSuccess=!!data.keepgoingOnSuccess
        //nb: error handler is created inside Workflow.fromMap
        return ce
    }

    public boolean isNodeStep(){
        return true;
    }
}