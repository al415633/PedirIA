package pythonAdapter.jsonPacker;

import pythonAdapter.jsonConverter.JSONCarneConverter;

public class JSONCarnePacker extends AbstractJSONPacker {
    public JSONCarnePacker(){
        converter = new JSONCarneConverter();
    }
}
