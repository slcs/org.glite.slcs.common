/*
 * Copyright (c) 2007-2009. Members of the EGEE Collaboration.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * $Id: SLCSCommonVersion.java,v 1.4 2009/07/31 12:45:51 vtschopp Exp $
 */
package org.glite.slcs;

public class SLCSCommonVersion {

    /** Version number */
    static public final String VERSION= "@version.number@";
    /** Version build */
    static public final String BUILD= "@version.build@";
    
    /** Copyright */
    static public final String COPYRIGHT= "Copyright (c) 2008-2009. Members of the EGEE Collaboration";

    /**
     * Prevents instantiation
     */
    private SLCSCommonVersion() {}

    /**
     * @return The version number in format MAJOR.MINOR.REVISION-BUILD
     */
    static public String getVersion() {
        StringBuffer sb= new StringBuffer();
        sb.append(VERSION);
        sb.append('-');
        sb.append(BUILD);
        return sb.toString();
    }

}
