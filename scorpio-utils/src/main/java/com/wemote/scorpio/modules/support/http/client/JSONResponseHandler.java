package com.wemote.scorpio.modules.support.http.client;

import com.google.common.collect.Maps;
import com.wemote.scorpio.modules.utils.JSONUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

public class JSONResponseHandler {

    private static final Logger LOG = LoggerFactory.getLogger(JSONResponseHandler.class);

    private static Map<String, ResponseHandler<?>> map = Maps.newHashMap();

    @SuppressWarnings("unchecked")
    public static <T> ResponseHandler<T> createResponseHandler(final Class<T> clazz) {

        if (map.containsKey(clazz.getName())) {
            return (ResponseHandler<T>) map.get(clazz.getName());
        } else {
            ResponseHandler<T> responseHandler = new ResponseHandler<T>() {
                @Override
                public T handleResponse(HttpResponse response)
                        throws ClientProtocolException, IOException {
                    StatusLine status = response.getStatusLine();
                    if (status.getStatusCode() >= HttpStatus.SC_OK && status.getStatusCode() < HttpStatus.SC_MULTIPLE_CHOICES) {
                        HttpEntity entity = response.getEntity();
                        String str = EntityUtils.toString(entity);

                        T t = JSONUtils.parseObject(new String(str.getBytes("iso-8859-1"), "utf-8"), clazz);
                        if (t == null) {
                            LOG.error("JSON parse error:{}", str);
                            // ignore;
                        }
                        return t;
                    } else {
                        LOG.warn(
                                "Did not receive successful HTTP response: status code = {}, status message = {}",
                                status.getStatusCode(), status.getReasonPhrase());
                        throw new ClientProtocolException("Unexpected response status: " + status);
                    }
                }
            };
            map.put(clazz.getName(), responseHandler);
            return responseHandler;
        }
    }

}
