package unification.tool.module.extent.output.uml.ldap;

import javax.naming.*;
import javax.naming.directory.*;
import java.util.Hashtable;

abstract class DirContextAdapter implements DirContext {
    protected abstract Attributes getAttributesForNonEmptyName();

    @Override public Attributes getAttributes(Name name) throws NamingException {
        return getAttributes(name.toString());
    }

    @Override public Attributes getAttributes(String name) throws NamingException {
        if (!name.isEmpty()) {
            throw new NameNotFoundException();
        }
        return getAttributesForNonEmptyName();
    }

    @Override public Attributes getAttributes(Name name, String[] attrIds) throws NamingException {
        return getAttributes(name.toString(), attrIds);
    }

    @Override public Attributes getAttributes(String name, String[] attributeIDs) throws NamingException {
        Attributes original = getAttributes(name);
        Attributes answer = new BasicAttributes(true);
        for (String attributeID : attributeIDs) {
            Attribute attribute = original.get(attributeID);
            if (attribute != null) {
                answer.put(attribute);
            }
        }
        return answer;
    }

    @Override public void modifyAttributes(Name name, int mod_op, Attributes attrs) throws NamingException {
        throw new UnsupportedOperationException();
    }

    @Override public void modifyAttributes(String name, int mod_op, Attributes attrs) throws NamingException {
        throw new UnsupportedOperationException();
    }

    @Override public void modifyAttributes(Name name, ModificationItem[] mods) throws NamingException {
        throw new UnsupportedOperationException();
    }

    @Override public void modifyAttributes(String name, ModificationItem[] mods) throws NamingException {
        throw new UnsupportedOperationException();
    }

    @Override public void bind(Name name, Object obj, Attributes attrs) throws NamingException {
        throw new UnsupportedOperationException();
    }

    @Override public void bind(String name, Object obj, Attributes attrs) throws NamingException {
        throw new UnsupportedOperationException();
    }

    @Override public void rebind(Name name, Object obj, Attributes attrs) throws NamingException {
        throw new UnsupportedOperationException();
    }

    @Override public void rebind(String name, Object obj, Attributes attrs) throws NamingException {
        throw new UnsupportedOperationException();
    }

    @Override public DirContext createSubcontext(Name name, Attributes attrs) throws NamingException {
        throw new UnsupportedOperationException();
    }

    @Override public DirContext createSubcontext(String name, Attributes attrs) throws NamingException {
        throw new UnsupportedOperationException();
    }

    @Override public DirContext getSchema(Name name) throws NamingException {
        throw new UnsupportedOperationException();
    }

    @Override public DirContext getSchema(String name) throws NamingException {
        throw new UnsupportedOperationException();
    }

    @Override public DirContext getSchemaClassDefinition(Name name) throws NamingException {
        throw new UnsupportedOperationException();
    }

    @Override public DirContext getSchemaClassDefinition(String name) throws NamingException {
        throw new UnsupportedOperationException();
    }

    @Override public NamingEnumeration<SearchResult> search(Name name, Attributes matchingAttributes, String[] attributesToReturn)
            throws NamingException {
        throw new UnsupportedOperationException();
    }

    @Override public NamingEnumeration<SearchResult> search(String name, Attributes matchingAttributes, String[] attributesToReturn)
            throws NamingException {
        throw new UnsupportedOperationException();
    }

    @Override public NamingEnumeration<SearchResult> search(Name name, Attributes matchingAttributes) throws NamingException {
        throw new UnsupportedOperationException();
    }

    @Override public NamingEnumeration<SearchResult> search(String name, Attributes matchingAttributes) throws NamingException {
        throw new UnsupportedOperationException();
    }

    @Override public NamingEnumeration<SearchResult> search(Name name, String filter, SearchControls cons) throws NamingException {
        throw new UnsupportedOperationException();
    }

    @Override public NamingEnumeration<SearchResult> search(String name, String filter, SearchControls cons) throws NamingException {
        throw new UnsupportedOperationException();
    }

    @Override public NamingEnumeration<SearchResult> search(Name name, String filterExpr, Object[] filterArgs, SearchControls cons)
            throws NamingException {
        throw new UnsupportedOperationException();
    }

    @Override public NamingEnumeration<SearchResult> search(String name, String filterExpr, Object[] filterArgs, SearchControls cons)
            throws NamingException {
        throw new UnsupportedOperationException();
    }

    @Override public Object lookup(Name name) throws NamingException {
        throw new UnsupportedOperationException();
    }

    @Override public Object lookup(String name) throws NamingException {
        throw new UnsupportedOperationException();
    }

    @Override public void bind(Name name, Object obj) throws NamingException {
        throw new UnsupportedOperationException();
    }

    @Override public void bind(String name, Object obj) throws NamingException {
        throw new UnsupportedOperationException();
    }

    @Override public void rebind(Name name, Object obj) throws NamingException {
        throw new UnsupportedOperationException();
    }

    @Override public void rebind(String name, Object obj) throws NamingException {
        throw new UnsupportedOperationException();
    }

    @Override public void unbind(Name name) throws NamingException {
        throw new UnsupportedOperationException();
    }

    @Override public void unbind(String name) throws NamingException {
        throw new UnsupportedOperationException();
    }

    @Override public void rename(Name oldName, Name newName) throws NamingException {
        throw new UnsupportedOperationException();
    }

    @Override public void rename(String oldName, String newName) throws NamingException {
        throw new UnsupportedOperationException();
    }

    @Override public NamingEnumeration<NameClassPair> list(Name name) throws NamingException {
        throw new UnsupportedOperationException();
    }

    @Override public NamingEnumeration<NameClassPair> list(String name) throws NamingException {
        throw new UnsupportedOperationException();
    }

    @Override public NamingEnumeration<Binding> listBindings(Name name) throws NamingException {
        throw new UnsupportedOperationException();
    }

    @Override public NamingEnumeration<Binding> listBindings(String name) throws NamingException {
        throw new UnsupportedOperationException();
    }

    @Override public void destroySubcontext(Name name) throws NamingException {
        throw new UnsupportedOperationException();
    }

    @Override public void destroySubcontext(String name) throws NamingException {
        throw new UnsupportedOperationException();
    }

    @Override public Context createSubcontext(Name name) throws NamingException {
        throw new UnsupportedOperationException();
    }

    @Override public Context createSubcontext(String name) throws NamingException {
        throw new UnsupportedOperationException();
    }

    @Override public Object lookupLink(Name name) throws NamingException {
        throw new UnsupportedOperationException();
    }

    @Override public Object lookupLink(String name) throws NamingException {
        throw new UnsupportedOperationException();
    }

    @Override public NameParser getNameParser(Name name) throws NamingException {
        throw new UnsupportedOperationException();
    }

    @Override public NameParser getNameParser(String name) throws NamingException {
        throw new UnsupportedOperationException();
    }

    @Override public Name composeName(Name name, Name prefix) throws NamingException {
        throw new UnsupportedOperationException();
    }

    @Override public String composeName(String name, String prefix) throws NamingException {
        throw new UnsupportedOperationException();
    }

    @Override public Object addToEnvironment(String propName, Object propVal) throws NamingException {
        throw new UnsupportedOperationException();
    }

    @Override public Object removeFromEnvironment(String propName) throws NamingException {
        throw new UnsupportedOperationException();
    }

    @Override public Hashtable<?, ?> getEnvironment() throws NamingException {
        throw new UnsupportedOperationException();
    }

    @Override public void close() throws NamingException {
        throw new UnsupportedOperationException();
    }

    @Override public String getNameInNamespace() throws NamingException {
        throw new UnsupportedOperationException();
    }
}
