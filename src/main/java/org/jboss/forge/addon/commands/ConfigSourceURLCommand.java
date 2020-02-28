package org.jboss.forge.addon.commands;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import org.eclipse.microprofile.config.ConfigProvider;
import org.jboss.forge.addon.config.ForgeConfigSourceProvider;
import org.jboss.forge.addon.configuration.Configuration;
import org.jboss.forge.addon.ui.command.AbstractUICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.furnace.util.Strings;


public class ConfigSourceURLCommand extends AbstractUICommand {

	@Inject
	private Configuration configuration;

	@Inject
	@WithAttributes(label = "add")
	private UIInput<String> add;

	@Inject
	@WithAttributes(label = "remove")
	private UISelectOne<String> remove;

	@Inject
	@WithAttributes(label = "list config-sources")
	private UIInput<Boolean> list;


	@Override
	public UICommandMetadata getMetadata(final UIContext context) {
		return Metadata.forCommand(ConfigSourceURLCommand.class)
				.name("Config Source")
				.description("MicroProfile ConfigSource configuration")
				.category(Categories.create("Configuration"));
	}

	@Override
	public void initializeUI(final UIBuilder builder) throws Exception {
		final String[] url = configuration.getStringArray(ForgeConfigSourceProvider.CONFIGSOURCE_PROPERTIES_URL_KEY);
		if (url != null) {
			remove.setValueChoices(Arrays.asList(url));
		}
		builder.add(remove);
		builder.add(add);
		builder.add(list);
	}

	@Override
	public Result execute(final UIExecutionContext context) throws Exception {
		if (list.getValue()) {
			ConfigProvider.getConfig().getConfigSources().forEach(System.out::println);
			return Results.success();
		}

		if (remove.hasValue()){
			String[] newUrls = 
				Arrays.stream(configuration.getStringArray(ForgeConfigSourceProvider.CONFIGSOURCE_PROPERTIES_URL_KEY))
					.filter(url -> !url.equals(remove.getValue()))
					.filter(url -> !Strings.isNullOrEmpty(url))
					.toArray(String[]::new);
				
			configuration.setProperty(ForgeConfigSourceProvider.CONFIGSOURCE_PROPERTIES_URL_KEY, newUrls);
			return Results.success("Config Source URL " + remove.getValue() + " removed!");
		}

		try {
			new URL(add.getValue());
		} catch (final MalformedURLException e) {
			return Results.fail(e.getMessage());
		}

		Set<String> newUrls = 
			new HashSet<String>(Arrays.asList(
					configuration.getStringArray(ForgeConfigSourceProvider.CONFIGSOURCE_PROPERTIES_URL_KEY)
				));
		newUrls.add(add.getValue());
		newUrls.removeIf(Strings::isEmpty);
		configuration.setProperty(ForgeConfigSourceProvider.CONFIGSOURCE_PROPERTIES_URL_KEY, newUrls);
		return Results.success("Configsource " + add.getValue() + " added!");
	}
}