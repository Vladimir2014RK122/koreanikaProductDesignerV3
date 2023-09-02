package PortalClient.Update;

import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;

public interface PortalResponseHandler {

    public void ResponseSuccess(SimpleHttpRequest request, SimpleHttpResponse response);
    public void ResponseFail(SimpleHttpRequest request, SimpleHttpResponse response);
}
