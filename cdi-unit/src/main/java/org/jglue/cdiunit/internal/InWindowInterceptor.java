package org.jglue.cdiunit.internal;

import javax.enterprise.inject.UnsatisfiedResolutionException;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.servlet.http.HttpServletRequest;

import org.jglue.cdiunit.ContextController;
import org.jglue.cdiunit.InWindowScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Interceptor
@InWindowScope
public class InWindowInterceptor {
	private static Logger log = LoggerFactory.getLogger(InWindowInterceptor.class);
	
	
	@Inject
	private ContextController _contextController;

	@Inject
	@CdiUnitRequest
	private Provider<Object> _requestProvider;

	@Inject
	private Provider<HttpServletRequest> _cdi1Provider;

	@AroundInvoke
	public Object around(InvocationContext ctx) throws Exception {
		try {
			HttpServletRequest httpServletRequest;
			try {
				httpServletRequest = (HttpServletRequest)_requestProvider.get();
			}
			catch(UnsatisfiedResolutionException e) {
				httpServletRequest = _cdi1Provider.get();
			}
			_contextController.openWindow(httpServletRequest);
			return ctx.proceed();
		} catch(Exception e) {
			log.error("Failed to open conversation context. This can occur is you are using cal10n-0.7.4, see http://jira.qos.ch/browse/CAL-29", e);
			throw e;
		} finally {
			_contextController.closeWindow();
		}
	}


}
