package com.couchbase.cli;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.protocol.HTTP;

public class CouchbaseResponse {
	HttpResponse response;
	
	public CouchbaseResponse(HttpResponse response) {
		this.response = response;
	}
	
	public int getReturnCode() {
		return response.getStatusLine().getStatusCode();
	}
	
	public String getBody() {
		HttpEntity entity = response.getEntity();

		if (entity != null) {
			try {
				InputStream stream = entity.getContent();
				String charset = getContentCharSet(entity);

				if (stream == null)
					return "";

				if (charset == null) {
					charset = HTTP.DEFAULT_CONTENT_CHARSET;
				}

				Reader reader = new InputStreamReader(stream, charset);
				StringBuilder buffer = new StringBuilder();

				char[] tmp = new char[1024];
				int l;
				while ((l = reader.read(tmp)) != -1) {
					buffer.append(tmp, 0, l);
				}
				reader.close();
				return buffer.toString();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	private String getContentCharSet(final HttpEntity entity) {
		String charset = null;
		
		if (entity != null) {
			if (entity.getContentType() != null) {
				HeaderElement values[] = entity.getContentType().getElements();

				if (values.length > 0) {
					NameValuePair param = values[0].getParameterByName("charset");
					
					if (param != null) {
						charset = param.getValue();
					}
				}
			}
		}
		return charset;
	}
}
