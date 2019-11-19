/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.software_atelier.simpleflex.conf;

import java.util.List;

/**
 *
 * @author tk
 */
public interface ConfigGenerator {
    
    /**
     * 
     * @return 
     */
    public GlobalConfig globalConfig();
    
    /**
     * 
     * @return 
     */
    public List<DomainConfig> domainConfigs();
}
