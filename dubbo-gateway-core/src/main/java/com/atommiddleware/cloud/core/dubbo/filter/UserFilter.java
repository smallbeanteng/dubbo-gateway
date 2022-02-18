package com.atommiddleware.cloud.core.dubbo.filter;

import java.util.Map;

import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.RpcException;
import org.springframework.util.CollectionUtils;

import com.atommiddleware.cloud.security.cas.PrincipalObtain;

@Activate(group = CommonConstants.CONSUMER)
public class UserFilter implements Filter {

	private PrincipalObtain principalObtain;

	@Override
	public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
		try {
			if (null != principalObtain) {
				Map<String, String> mapPrincipal = principalObtain.getPrincipal();
				if (!CollectionUtils.isEmpty(mapPrincipal)) {
					RpcContext.getContext().setAttachments(mapPrincipal);
				}
			}
			return invoker.invoke(invocation);
		} finally {
			RpcContext.getContext().clearAttachments();
		}
	}

	public void setPrincipalObtain(PrincipalObtain principalObtain) {
		this.principalObtain = principalObtain;
	}

}
