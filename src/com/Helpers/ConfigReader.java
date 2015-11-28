package com.Helpers;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;

/**
 * Class to read config values for application
 */
public class ConfigReader
{
    private String pathToConfigFile = "config.yml";

    private String databaseInstance;

    private String databaseUser;

    private String databasePassword;

    private String databaseTable;

    /**
     * Get string that holds host, port, and database name information.
     * @return
     */
    public String getDatabaseInstance()
    {
        return databaseInstance;
    }

    /**
     * Get username for database.
     * @return
     */
    public String getDatabaseUser()
    {
        return databaseUser;
    }

    /**
     * Get password for user account to database.
     * @return
     */
    public String getDatabasePassword()
    {
        return databasePassword;
    }

    /**
     * Get table in database to store algorithm results.
     * @return
     */
    public String getDatabaseTable()
    {
        return databaseTable;
    }

    /**
     * Primary constructor.
     */
    public ConfigReader()
    {
        Map configValuesMap = null;
        try
        {
            Yaml yaml = new Yaml();

            configValuesMap = (Map)yaml.load(new FileInputStream(new File(pathToConfigFile)));

            setConfigProperties(configValuesMap);
        }
        catch (FileNotFoundException ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * Set the config properties of this class from file.
     * @param configValuesMap
     */
    private void setConfigProperties(Map configValuesMap)
    {
        databaseInstance = (String)configValuesMap.get("mysql_database");

        databaseUser = (String)configValuesMap.get("database_user");

        databasePassword = (String)configValuesMap.get("database_password");

        databaseTable = (String)configValuesMap.get("database_table");
    }
}
