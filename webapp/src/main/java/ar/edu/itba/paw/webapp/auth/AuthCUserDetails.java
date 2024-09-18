package ar.edu.itba.paw.webapp.auth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class AuthCUserDetails extends User {
    /* Esta clase nos permite guardar los datos de un models.User en un userdetails.User, una clase de spring security.
     * Una alternativa es hacer nuestra propia clase que implemente la interfaz userdetails.UserDetails.
     */

    private ar.edu.itba.paw.models.User user;

    public AuthCUserDetails(final ar.edu.itba.paw.models.User cuser, Collection<? extends GrantedAuthority> authorities) {
        super(cuser.getUsername(), cuser.getPassword(), authorities);
        this.user = cuser;
    }

    public AuthCUserDetails(final ar.edu.itba.paw.models.User cuser, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(cuser.getUsername(), cuser.getPassword(), enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.user = cuser;
    }

    public ar.edu.itba.paw.models.User getUser() {
        return user;
    }

}
