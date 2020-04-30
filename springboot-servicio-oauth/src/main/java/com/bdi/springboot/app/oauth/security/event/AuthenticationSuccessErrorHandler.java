package com.bdi.springboot.app.oauth.security.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.bdi.springboot.app.oauth.services.IUsuarioService;
import com.formacionbdi.springboot.app.usuarios.commons.models.entity.Usuario;

import brave.Tracer;
import feign.FeignException;

@Component
public class AuthenticationSuccessErrorHandler implements AuthenticationEventPublisher {

	Logger log = LoggerFactory.getLogger(AuthenticationEventPublisher.class);
	
	@Autowired
	private IUsuarioService usuarioService;
	
	@Autowired
	private Tracer tracer;
	
	@Override
	public void publishAuthenticationSuccess(Authentication authentication) {
		UserDetails user = (UserDetails)authentication.getPrincipal();
		log.info("Authentication Success: "+user.getUsername());
	}

	@Override
	public void publishAuthenticationFailure(AuthenticationException exception, Authentication authentication) {
		log.info("Authentication Fail "+exception.getMessage());
		StringBuilder sb = new StringBuilder();
		try 
		{
			Usuario usuario = usuarioService.findByUsername(authentication.getName());
			sb.append("Intentos actual es de usuario: "+usuario.getNombre());
			if (usuario.getIntentos()==null)
			{
				usuario.setIntentos(0);
			}
			usuario.setIntentos(usuario.getIntentos()+1);
			
			if (usuario.getIntentos()>=3)
			{
				usuario.setEnabled(false);
			}
			
			usuarioService.update(usuario, usuario.getId());
			sb.append(" - Intentos new: "+usuario.getIntentos());	
			tracer.currentSpan().tag("error.mensaje",sb.toString());
		} catch (FeignException e) {
			log.error(String.format("El usuario %s no existe en el sistema", authentication.getName()));
		}
		
	}

}
