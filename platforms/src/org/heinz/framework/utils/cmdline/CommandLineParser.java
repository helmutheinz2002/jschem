package org.heinz.framework.utils.cmdline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CommandLineParser {
	private static CommandLineParser instance;
	private static final String OPTION_START_CHAR = "-";
	
	private static Map optionsByName = new HashMap();
	private Map optionValuesByName = new HashMap();
	private List arguments = new ArrayList();
	private List unknownOptions = new ArrayList();
	private List errorOptions = new ArrayList();
	
	public static CommandLineParser instance() {
		if(instance == null)
			instance = new CommandLineParser();
		return instance;
	}
	
	public static void addOption(CommandLineOption option) {
		optionsByName.put(option.getName().toLowerCase(), option);
	}
	
	public void parseArguments(String[] args) {
		arguments.clear();
		optionValuesByName.clear();
		unknownOptions.clear();
		errorOptions.clear();
		
		int maxOptIdx = findLastOption(args);
		for(int i=0; i<maxOptIdx; i++) {
			String arg = args[i];
			arg = arg.substring(1).toLowerCase();
			CommandLineOption argument = (CommandLineOption) optionsByName.get(arg);
			if(argument != null) {
				if(argument.getArgumentClass() == null)
					optionValuesByName.put(arg, Boolean.TRUE);
				else {
					i++;
					try {
						Object value = argument.parse(args[i]);
						optionValuesByName.put(arg, value);
					} catch (Exception e) {
						errorOptions.add(argument);
					}
				}
			} else
				unknownOptions.add(args[i]);
		}
		
		for(int i=maxOptIdx; i<args.length; i++)
			arguments.add(args[i]);
	}
	
	public boolean hasErrors() {
		return errorOptions.size() > 0;
	}
	
	public boolean hasWarnings() {
		return unknownOptions.size() > 0;
	}
	
	public boolean hasArguments() {
		return arguments.size() > 0;
	}
	
	private int findLastOption(String[] args) {
		for(int i=args.length-1; i>= 0; i--)
			if(args[i].startsWith(OPTION_START_CHAR)) {
				String arg = args[i].substring(1).toLowerCase();
				i++;
				// add one position if we know this option and it needs a parameter
				CommandLineOption option = (CommandLineOption) optionsByName.get(arg);
				if((option != null) && (option.getArgumentClass() != null))
					i++;
				
				return i;
			}
		return 0;
	}
	
	public List getArguments() {
		return arguments;
	}
	
	public Object getOptionValue(CommandLineOption option) {
		Object value = optionValuesByName.get(option.getName().toLowerCase());
		if(value != null)
			return value;
		
		return option.getDefaultValue();
	}
	
	public String toString() {
		StringBuffer s = new StringBuffer("ArgumentParser:\n");
		for(Iterator it=optionsByName.keySet().iterator(); it.hasNext();) {
			String argName = (String) it.next();
			CommandLineOption arg = (CommandLineOption) optionsByName.get(argName);
			Object v = optionValuesByName.get(argName);
			
			s.append("  ");
			s.append(arg.getName());
			s.append(" = ");
			if(v != null)
				s.append(""+v);
			else {
				s.append("default(");
				s.append(arg.getDefaultValue());
				s.append(")");
			}
			s.append("\n");
		}
		
		s.append("\nArguments:\n");
		for(Iterator it=arguments.iterator(); it.hasNext();) {
			s.append("  ");
			s.append(""+it.next());
			s.append("\n");
		}
		
		if(hasErrors()) {
			s.append("\nErrors in options:\n");
			for(Iterator it=errorOptions.iterator(); it.hasNext();) {
				CommandLineOption option = (CommandLineOption) it.next(); 
				s.append("  ");
				s.append(""+option.getName());
				s.append("\n");
			}
		}
		
		if(hasWarnings()) {
			s.append("\nUnknown options:\n");
			for(Iterator it=unknownOptions.iterator(); it.hasNext();) {
				s.append("  ");
				s.append(""+it.next());
				s.append("\n");
			}
		}
		return s.toString();
	}
	
	public static void main(String[] args) throws Exception {
		CommandLineParser a = CommandLineParser.instance();
		CommandLineParser.addOption(new CommandLineOption("mdi", null, null));
		CommandLineParser.addOption(new CommandLineOption("h", Integer.class, new Integer(1)));
		CommandLineParser.addOption(new CommandLineOption("w", Integer.class, new Integer(2)));
		CommandLineParser.addOption(new CommandLineOption("z", Integer.class, new Integer(3)));
		a.parseArguments(new String[] { "-w", "34", "-h", "heinz", "-mdi", "file" });
		System.out.println(a);
	}
}
