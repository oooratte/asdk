package net.as_development.camel.core;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import javax.xml.namespace.QName;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.ErrorHandlerFactory;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Expression;
import org.apache.camel.LoggingLevel;
import org.apache.camel.NoSuchEndpointException;
import org.apache.camel.Predicate;
import org.apache.camel.Processor;
import org.apache.camel.Route;
import org.apache.camel.ServiceStatus;
import org.apache.camel.ShutdownRoute;
import org.apache.camel.ShutdownRunningTask;
import org.apache.camel.builder.DataFormatClause;
import org.apache.camel.builder.ExpressionClause;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.AOPDefinition;
import org.apache.camel.model.AggregateDefinition;
import org.apache.camel.model.ChoiceDefinition;
import org.apache.camel.model.DataFormatDefinition;
import org.apache.camel.model.DelayDefinition;
import org.apache.camel.model.DescriptionDefinition;
import org.apache.camel.model.DynamicRouterDefinition;
import org.apache.camel.model.EnrichDefinition;
import org.apache.camel.model.FilterDefinition;
import org.apache.camel.model.FromDefinition;
import org.apache.camel.model.IdempotentConsumerDefinition;
import org.apache.camel.model.LoadBalanceDefinition;
import org.apache.camel.model.LoopDefinition;
import org.apache.camel.model.ModelCamelContext;
import org.apache.camel.model.MulticastDefinition;
import org.apache.camel.model.OnCompletionDefinition;
import org.apache.camel.model.OnExceptionDefinition;
import org.apache.camel.model.PipelineDefinition;
import org.apache.camel.model.PolicyDefinition;
import org.apache.camel.model.PollEnrichDefinition;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.RecipientListDefinition;
import org.apache.camel.model.ResequenceDefinition;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.RoutingSlipDefinition;
import org.apache.camel.model.SamplingDefinition;
import org.apache.camel.model.SortDefinition;
import org.apache.camel.model.SplitDefinition;
import org.apache.camel.model.ThreadsDefinition;
import org.apache.camel.model.ThrottleDefinition;
import org.apache.camel.model.TransactedDefinition;
import org.apache.camel.model.TryDefinition;
import org.apache.camel.model.ValidateDefinition;
import org.apache.camel.model.WireTapDefinition;
import org.apache.camel.model.language.ExpressionDefinition;
import org.apache.camel.model.rest.RestDefinition;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.apache.camel.processor.loadbalancer.LoadBalancer;
import org.apache.camel.spi.DataFormat;
import org.apache.camel.spi.IdempotentRepository;
import org.apache.camel.spi.InterceptStrategy;
import org.apache.camel.spi.NodeIdFactory;
import org.apache.camel.spi.Policy;
import org.apache.camel.spi.RouteContext;
import org.apache.camel.spi.RoutePolicy;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;

//=============================================================================
public class CamelInterceptedRouteDefinition extends RouteDefinition
{
	//-------------------------------------------------------------------------
	public CamelInterceptedRouteDefinition (final CamelRouteGroup aParent ,
					   		                final RouteDefinition aWrapped)
		throws Exception
	{
		Validate.notNull(aParent , "Invalid argument 'parent'." );
		Validate.notNull(aWrapped, "Invalid argument 'wrapped'.");

		m_aParent  = aParent ;
		m_aWrapped = aWrapped;
	}

	//-------------------------------------------------------------------------
	@Override
	public ProcessorDefinition< ? > end()
	{
		try
		{
	  		final ProcessorDefinition< ? >                                 aRoute     = m_aWrapped.end ();
	  		final Map< String, WeakReference< ProcessorDefinition< ? > > > lRouteRefs = m_aParent.mem_RouteRefs();
	  		final WeakReference< ProcessorDefinition< ? > >                rRoute     = new WeakReference< ProcessorDefinition< ? > >(aRoute);
	  		final String                                                   sRouteID   = StringUtils.defaultIfEmpty(aRoute.getId (), m_sRouteID);
	
	  		lRouteRefs.put(sRouteID, rRoute);
				return aRoute;
		}
		catch (Throwable ex)
		{
			throw new RuntimeException (ex);
		}
	}

	//-------------------------------------------------------------------------
	public int hashCode() {
		return m_aWrapped.hashCode();
	}

	public String getId() {
		return m_aWrapped.getId();
	}

	public void setId(String value) {
		m_aWrapped.setId(value);
	}

	public DescriptionDefinition getDescription() {
		return m_aWrapped.getDescription();
	}

	public void setDescription(DescriptionDefinition description) {
		m_aWrapped.setDescription(description);
	}

	public String getShortName() {
		return m_aWrapped.getShortName();
	}

	public RouteDefinition description(String text) {
		m_aWrapped = m_aWrapped.description(text);
		return this;
	}

	public boolean equals(Object obj) {
		return m_aWrapped.equals(obj);
	}

	public RouteDefinition description(String id, String text, String lang) {
		m_aWrapped = m_aWrapped.description(id, text, lang);
		return this;
	}

	public void fromRest(String uri) {
		m_aWrapped.fromRest(uri);
	}

	public void prepare(ModelCamelContext context) {
		m_aWrapped.prepare(context);
	}

	public int getIndex() {
		return m_aWrapped.getIndex();
	}

	public void markPrepared() {
		m_aWrapped.markPrepared();
	}

	public String idOrCreate(NodeIdFactory factory) {
		return m_aWrapped.idOrCreate(factory);
	}

	public boolean isTopLevelOnly() {
		return m_aWrapped.isTopLevelOnly();
	}

	public void markUnprepared() {
		m_aWrapped.markUnprepared();
	}

	public Boolean getCustomId() {
		return m_aWrapped.getCustomId();
	}

	public void setCustomId(Boolean customId) {
		m_aWrapped.setCustomId(customId);
	}

	public boolean isAbstract() {
		return m_aWrapped.isAbstract();
	}

	public String toString() {
		return m_aWrapped.toString();
	}

	public boolean hasCustomIdAssigned() {
		return m_aWrapped.hasCustomIdAssigned();
	}

	public String getDescriptionText() {
		return m_aWrapped.getDescriptionText();
	}

	public ServiceStatus getStatus(CamelContext camelContext) {
		return m_aWrapped.getStatus(camelContext);
	}

	public boolean isStartable(CamelContext camelContext) {
		return m_aWrapped.isStartable(camelContext);
	}

	public boolean isStoppable(CamelContext camelContext) {
		return m_aWrapped.isStoppable(camelContext);
	}

	public Processor createProcessor(RouteContext routeContext)
			throws Exception {
		return m_aWrapped.createProcessor(routeContext);
	}

	public List<RouteContext> addRoutes(ModelCamelContext camelContext,
			Collection<Route> routes) throws Exception {
		return m_aWrapped.addRoutes(camelContext, routes);
	}

	public Processor createOutputsProcessor(RouteContext routeContext)
			throws Exception {
		return m_aWrapped.createOutputsProcessor(routeContext);
	}

	public Processor createChildProcessor(RouteContext routeContext,
			boolean mandatory) throws Exception {
		return m_aWrapped.createChildProcessor(routeContext, mandatory);
	}

	public Endpoint resolveEndpoint(CamelContext camelContext, String uri)
			throws NoSuchEndpointException {
		return m_aWrapped.resolveEndpoint(camelContext, uri);
	}

	public RouteDefinition adviceWith(CamelContext camelContext,
			RouteBuilder builder) throws Exception {
		m_aWrapped = m_aWrapped.adviceWith(camelContext, builder);
		return this;
	}

	public RouteDefinition adviceWith(ModelCamelContext camelContext,
			RouteBuilder builder) throws Exception {
		m_aWrapped = m_aWrapped.adviceWith(camelContext, builder);
		return this;
	}

	public void addOutput(ProcessorDefinition<?> output) {
		m_aWrapped.addOutput(output);
	}

	public void clearOutput() {
		m_aWrapped.clearOutput();
	}

	public void addRoutes(RouteContext routeContext, Collection<Route> routes)
			throws Exception {
		m_aWrapped.addRoutes(routeContext, routes);
	}

	public Processor wrapProcessor(RouteContext routeContext,
			Processor processor) throws Exception {
		return m_aWrapped.wrapProcessor(routeContext, processor);
	}

	public RouteDefinition from(String uri) {
		m_aWrapped = m_aWrapped.from(uri);
		return this;
	}

	public RouteDefinition from(Endpoint endpoint) {
		m_aWrapped = m_aWrapped.from(endpoint);
		return this;
	}

	public RouteDefinition from(String... uris) {
		m_aWrapped = m_aWrapped.from(uris);
		return this;
	}

	public RouteDefinition from(Endpoint... endpoints) {
		m_aWrapped = m_aWrapped.from(endpoints);
		return this;
	}

	public RouteDefinition group(String name) {
		m_aWrapped = m_aWrapped.group(name);
		return this;
	}

	public RouteDefinition routeId(String id) {
		m_sRouteID = id;
		m_aWrapped = m_aWrapped.routeId(id);
		return this;
	}

	public RouteDefinition routeDescription(String description) {
		m_aWrapped = m_aWrapped.routeDescription(description);
		return this;
	}

	public RouteDefinition noStreamCaching() {
		m_aWrapped = m_aWrapped.noStreamCaching();
		return this;
	}

	public RouteDefinition streamCaching() {
		m_aWrapped = m_aWrapped.streamCaching();
		return this;
	}

	public RouteDefinition streamCaching(String streamCache) {
		m_aWrapped = m_aWrapped.streamCaching(streamCache);
		return this;
	}

	public RouteDefinition noTracing() {
		m_aWrapped = m_aWrapped.noTracing();
		return this;
	}

	public RouteDefinition tracing() {
		m_aWrapped = m_aWrapped.tracing();
		return this;
	}

	public RouteDefinition tracing(String tracing) {
		m_aWrapped = m_aWrapped.tracing(tracing);
		return this;
	}

	public RouteDefinition messageHistory() {
		m_aWrapped = m_aWrapped.messageHistory();
		return this;
	}

	public RouteDefinition messageHistory(String messageHistory) {
		m_aWrapped = m_aWrapped.messageHistory(messageHistory);
		return this;
	}

	public RouteDefinition noMessageHistory() {
		m_aWrapped = m_aWrapped.noMessageHistory();
		return this;
	}

	public RouteDefinition noHandleFault() {
		m_aWrapped = m_aWrapped.noHandleFault();
		return this;
	}

	public RouteDefinition handleFault() {
		m_aWrapped = m_aWrapped.handleFault();
		return this;
	}

	public RouteDefinition noDelayer() {
		m_aWrapped = m_aWrapped.noDelayer();
		return this;
	}

	public RouteDefinition delayer(long delay) {
		m_aWrapped = m_aWrapped.delayer(delay);
		return this;
	}

	public RouteDefinition errorHandler(ErrorHandlerFactory errorHandlerBuilder) {
		m_aWrapped = m_aWrapped.errorHandler(errorHandlerBuilder);
		return this;
	}

	public RouteDefinition noAutoStartup() {
		m_aWrapped = m_aWrapped.noAutoStartup();
		return this;
	}

	public RouteDefinition autoStartup(String autoStartup) {
		m_aWrapped = m_aWrapped.autoStartup(autoStartup);
		return this;
	}

	public RouteDefinition autoStartup(boolean autoStartup) {
		m_aWrapped = m_aWrapped.autoStartup(autoStartup);
		return this;
	}

	public RouteDefinition startupOrder(int order) {
		m_aWrapped = m_aWrapped.startupOrder(order);
		return this;
	}

	public RouteDefinition routePolicy(RoutePolicy... policies) {
		m_aWrapped = m_aWrapped.routePolicy(policies);
		return this;
	}

	public RouteDefinition routePolicyRef(String routePolicyRef) {
		m_aWrapped = m_aWrapped.routePolicyRef(routePolicyRef);
		return this;
	}

	public RouteDefinition shutdownRoute(ShutdownRoute shutdownRoute) {
		m_aWrapped = m_aWrapped.shutdownRoute(shutdownRoute);
		return this;
	}

	public RouteDefinition shutdownRunningTask(
			ShutdownRunningTask shutdownRunningTask) {
		m_aWrapped = m_aWrapped.shutdownRunningTask(shutdownRunningTask);
		return this;
	}

	public List<FromDefinition> getInputs() {
		return m_aWrapped.getInputs();
	}

	public void setInputs(List<FromDefinition> inputs) {
		m_aWrapped.setInputs(inputs);
	}

	public List<ProcessorDefinition<?>> getOutputs() {
		return m_aWrapped.getOutputs();
	}

	public void setOutputs(List<ProcessorDefinition<?>> outputs) {
		m_aWrapped.setOutputs(outputs);
	}

	public boolean isOutputSupported() {
		return m_aWrapped.isOutputSupported();
	}

	public String getGroup() {
		return m_aWrapped.getGroup();
	}

	public void setGroup(String group) {
		m_aWrapped.setGroup(group);
	}

	public String getStreamCache() {
		return m_aWrapped.getStreamCache();
	}

	public void setStreamCache(String streamCache) {
		m_aWrapped.setStreamCache(streamCache);
	}

	public String getTrace() {
		return m_aWrapped.getTrace();
	}

	public void setTrace(String trace) {
		m_aWrapped.setTrace(trace);
	}

	public String getMessageHistory() {
		return m_aWrapped.getMessageHistory();
	}

	public void setMessageHistory(String messageHistory) {
		m_aWrapped.setMessageHistory(messageHistory);
	}

	public String getHandleFault() {
		return m_aWrapped.getHandleFault();
	}

	public void setHandleFault(String handleFault) {
		m_aWrapped.setHandleFault(handleFault);
	}

	public String getDelayer() {
		return m_aWrapped.getDelayer();
	}

	public void setDelayer(String delayer) {
		m_aWrapped.setDelayer(delayer);
	}

	public String getAutoStartup() {
		return m_aWrapped.getAutoStartup();
	}

	public boolean isAutoStartup(CamelContext camelContext) throws Exception {
		return m_aWrapped.isAutoStartup(camelContext);
	}

	public void setAutoStartup(String autoStartup) {
		m_aWrapped.setAutoStartup(autoStartup);
	}

	public Integer getStartupOrder() {
		return m_aWrapped.getStartupOrder();
	}

	public void configureChild(ProcessorDefinition<?> output) {
		m_aWrapped.configureChild(output);
	}

	public void setStartupOrder(Integer startupOrder) {
		m_aWrapped.setStartupOrder(startupOrder);
	}

	public RouteDefinition placeholder(String option, String key) {
		m_aWrapped = m_aWrapped.placeholder(option, key);
		return this;
	}

	public void setErrorHandlerRef(String errorHandlerRef) {
		m_aWrapped.setErrorHandlerRef(errorHandlerRef);
	}

	public String getErrorHandlerRef() {
		return m_aWrapped.getErrorHandlerRef();
	}

	public RouteDefinition attribute(QName name, Object value) {
		m_aWrapped = m_aWrapped.attribute(name, value);
		return this;
	}

	public void setErrorHandlerBuilderIfNull(
			ErrorHandlerFactory errorHandlerBuilder) {
		m_aWrapped.setErrorHandlerBuilderIfNull(errorHandlerBuilder);
	}

	public void setRoutePolicyRef(String routePolicyRef) {
		m_aWrapped.setRoutePolicyRef(routePolicyRef);
	}

	public RouteDefinition to(String uri) {
		m_aWrapped = m_aWrapped.to(uri);
		return this;
	}

	public String getRoutePolicyRef() {
		return m_aWrapped.getRoutePolicyRef();
	}

	public RouteDefinition toD(String uri) {
		m_aWrapped = m_aWrapped.toD(uri);
		return this;
	}

	public List<RoutePolicy> getRoutePolicies() {
		return m_aWrapped.getRoutePolicies();
	}

	public void setRoutePolicies(List<RoutePolicy> routePolicies) {
		m_aWrapped.setRoutePolicies(routePolicies);
	}

	public RouteDefinition toD(String uri, boolean ignoreInvalidEndpoint) {
		m_aWrapped = m_aWrapped.toD(uri, ignoreInvalidEndpoint);
		return this;
	}

	public ShutdownRoute getShutdownRoute() {
		return m_aWrapped.getShutdownRoute();
	}

	public void setShutdownRoute(ShutdownRoute shutdownRoute) {
		m_aWrapped.setShutdownRoute(shutdownRoute);
	}

	public ShutdownRunningTask getShutdownRunningTask() {
		return m_aWrapped.getShutdownRunningTask();
	}

	public RouteDefinition toF(String uri, Object... args) {
		m_aWrapped = m_aWrapped.toF(uri, args);
		return this;
	}

	public void setShutdownRunningTask(ShutdownRunningTask shutdownRunningTask) {
		m_aWrapped.setShutdownRunningTask(shutdownRunningTask);
	}

	public RouteDefinition to(Endpoint endpoint) {
		m_aWrapped = m_aWrapped.to(endpoint);
		return this;
	}

	public ErrorHandlerFactory getErrorHandlerBuilder() {
		return m_aWrapped.getErrorHandlerBuilder();
	}

	public RouteDefinition to(ExchangePattern pattern, String uri) {
		m_aWrapped = m_aWrapped.to(pattern, uri);
		return this;
	}

	public void setErrorHandlerBuilder(ErrorHandlerFactory errorHandlerBuilder) {
		m_aWrapped.setErrorHandlerBuilder(errorHandlerBuilder);
	}

	public Boolean isRest() {
		return m_aWrapped.isRest();
	}

	public RestDefinition getRestDefinition() {
		return m_aWrapped.getRestDefinition();
	}

	public RouteDefinition to(ExchangePattern pattern, Endpoint endpoint) {
		m_aWrapped = m_aWrapped.to(pattern, endpoint);
		return this;
	}

	public void setRestDefinition(RestDefinition restDefinition) {
		m_aWrapped.setRestDefinition(restDefinition);
	}

	public boolean isContextScopedErrorHandler(CamelContext context) {
		return m_aWrapped.isContextScopedErrorHandler(context);
	}

	public RouteDefinition to(String... uris) {
		m_aWrapped = m_aWrapped.to(uris);
		return this;
	}

	public RouteDefinition to(Endpoint... endpoints) {
		m_aWrapped = m_aWrapped.to(endpoints);
		return this;
	}

	public RouteDefinition to(Iterable<Endpoint> endpoints) {
		m_aWrapped = m_aWrapped.to(endpoints);
		return this;
	}

	public RouteDefinition to(ExchangePattern pattern, String... uris) {
		m_aWrapped = m_aWrapped.to(pattern, uris);
		return this;
	}

	public RouteDefinition to(ExchangePattern pattern, Endpoint... endpoints) {
		m_aWrapped = m_aWrapped.to(pattern, endpoints);
		return this;
	}

	public RouteDefinition to(ExchangePattern pattern,
			Iterable<Endpoint> endpoints) {
		m_aWrapped = m_aWrapped.to(pattern, endpoints);
		return this;
	}

	public RouteDefinition setExchangePattern(ExchangePattern exchangePattern) {
		m_aWrapped = m_aWrapped.setExchangePattern(exchangePattern);
		return this;
	}

	public RouteDefinition inOnly() {
		m_aWrapped = m_aWrapped.inOnly();
		return this;
	}

	public RouteDefinition inOnly(String uri) {
		m_aWrapped = m_aWrapped.inOnly(uri);
		return this;
	}

	public RouteDefinition inOnly(Endpoint endpoint) {
		m_aWrapped = m_aWrapped.inOnly(endpoint);
		return this;
	}

	public RouteDefinition inOnly(String... uris) {
		m_aWrapped = m_aWrapped.inOnly(uris);
		return this;
	}

	public RouteDefinition inOnly(Endpoint... endpoints) {
		m_aWrapped = m_aWrapped.inOnly(endpoints);
		return this;
	}

	public RouteDefinition inOnly(Iterable<Endpoint> endpoints) {
		m_aWrapped = m_aWrapped.inOnly(endpoints);
		return this;
	}

	public RouteDefinition inOut() {
		m_aWrapped = m_aWrapped.inOut();
		return this;
	}

	public RouteDefinition inOut(String uri) {
		m_aWrapped = m_aWrapped.inOut(uri);
		return this;
	}

	public RouteDefinition inOut(Endpoint endpoint) {
		m_aWrapped = m_aWrapped.inOut(endpoint);
		return this;
	}

	public RouteDefinition inOut(String... uris) {
		m_aWrapped = m_aWrapped.inOut(uris);
		return this;
	}

	public RouteDefinition inOut(Endpoint... endpoints) {
		m_aWrapped = m_aWrapped.inOut(endpoints);
		return this;
	}

	public RouteDefinition inOut(Iterable<Endpoint> endpoints) {
		m_aWrapped = m_aWrapped.inOut(endpoints);
		return this;
	}

	public RouteDefinition id(String id) {
		m_aWrapped = m_aWrapped.id(id);
		return this;
	}

	public MulticastDefinition multicast() {
		return m_aWrapped.multicast();
	}

	public MulticastDefinition multicast(
			AggregationStrategy aggregationStrategy, boolean parallelProcessing) {
		return m_aWrapped.multicast(aggregationStrategy, parallelProcessing);
	}

	public MulticastDefinition multicast(AggregationStrategy aggregationStrategy) {
		return m_aWrapped.multicast(aggregationStrategy);
	}

	public PipelineDefinition pipeline() {
		return m_aWrapped.pipeline();
	}

	public RouteDefinition pipeline(String... uris) {
		m_aWrapped = m_aWrapped.pipeline(uris);
		return this;
	}

	public RouteDefinition pipeline(Endpoint... endpoints) {
		m_aWrapped = m_aWrapped.pipeline(endpoints);
		return this;
	}

	public RouteDefinition pipeline(Collection<Endpoint> endpoints) {
		m_aWrapped = m_aWrapped.pipeline(endpoints);
		return this;
	}

	public ThreadsDefinition threads() {
		return m_aWrapped.threads();
	}

	public ThreadsDefinition threads(int poolSize) {
		return m_aWrapped.threads(poolSize);
	}

	public ThreadsDefinition threads(int poolSize, int maxPoolSize) {
		return m_aWrapped.threads(poolSize, maxPoolSize);
	}

	public ThreadsDefinition threads(int poolSize, int maxPoolSize,
			String threadName) {
		return m_aWrapped.threads(poolSize, maxPoolSize, threadName);
	}

	public AOPDefinition aop() {
		return m_aWrapped.aop();
	}

	public ProcessorDefinition<?> endParent() {
		return m_aWrapped.endParent();
	}

	public ChoiceDefinition endChoice() {
		return m_aWrapped.endChoice();
	}

	public RestDefinition endRest() {
		return m_aWrapped.endRest();
	}

	public TryDefinition endDoTry() {
		return m_aWrapped.endDoTry();
	}

	public IdempotentConsumerDefinition idempotentConsumer(
			Expression messageIdExpression) {
		return m_aWrapped.idempotentConsumer(messageIdExpression);
	}

	public IdempotentConsumerDefinition idempotentConsumer(
			Expression messageIdExpression,
			IdempotentRepository<?> idempotentRepository) {
		return m_aWrapped.idempotentConsumer(messageIdExpression,
				idempotentRepository);
	}

	public ExpressionClause<IdempotentConsumerDefinition> idempotentConsumer(
			IdempotentRepository<?> idempotentRepository) {
		return m_aWrapped.idempotentConsumer(idempotentRepository);
	}

	public ExpressionClause<? extends FilterDefinition> filter() {
		return m_aWrapped.filter();
	}

	public FilterDefinition filter(Predicate predicate) {
		return m_aWrapped.filter(predicate);
	}

	public FilterDefinition filter(ExpressionDefinition expression) {
		return m_aWrapped.filter(expression);
	}

	public FilterDefinition filter(String language, String expression) {
		return m_aWrapped.filter(language, expression);
	}

	public ValidateDefinition validate(Expression expression) {
		return m_aWrapped.validate(expression);
	}

	public ValidateDefinition validate(Predicate predicate) {
		return m_aWrapped.validate(predicate);
	}

	public ExpressionClause<ValidateDefinition> validate() {
		return m_aWrapped.validate();
	}

	public LoadBalanceDefinition loadBalance() {
		return m_aWrapped.loadBalance();
	}

	public LoadBalanceDefinition loadBalance(LoadBalancer loadBalancer) {
		return m_aWrapped.loadBalance(loadBalancer);
	}

	public RouteDefinition log(String message) {
		m_aWrapped = m_aWrapped.log(message);
		return this;
	}

	public RouteDefinition log(LoggingLevel loggingLevel, String message) {
		m_aWrapped = m_aWrapped.log(loggingLevel, message);
		return this;
	}

	public RouteDefinition log(LoggingLevel loggingLevel, String logName,
			String message) {
		m_aWrapped = m_aWrapped.log(loggingLevel, logName, message);
		return this;
	}

	public RouteDefinition log(LoggingLevel loggingLevel, Logger logger,
			String message) {
		m_aWrapped = m_aWrapped.log(loggingLevel, logger, message);
		return this;
	}

	public RouteDefinition log(LoggingLevel loggingLevel, String logName,
			String marker, String message) {
		m_aWrapped = m_aWrapped.log(loggingLevel, logName, marker, message);
		return this;
	}

	public RouteDefinition log(LoggingLevel loggingLevel, Logger logger,
			String marker, String message) {
		m_aWrapped = m_aWrapped.log(loggingLevel, logger, marker, message);
		return this;
	}

	public ChoiceDefinition choice() {
		return m_aWrapped.choice();
	}

	public TryDefinition doTry() {
		return m_aWrapped.doTry();
	}

	public RecipientListDefinition<RouteDefinition> recipientList(
			Expression recipients) {
		return m_aWrapped.recipientList(recipients);
	}

	public RecipientListDefinition<RouteDefinition> recipientList(
			Expression recipients, String delimiter) {
		return m_aWrapped.recipientList(recipients, delimiter);
	}

	public ExpressionClause<RecipientListDefinition<RouteDefinition>> recipientList(
			String delimiter) {
		return m_aWrapped.recipientList(delimiter);
	}

	public ExpressionClause<RecipientListDefinition<RouteDefinition>> recipientList() {
		return m_aWrapped.recipientList();
	}

	public RouteDefinition routingSlip(String header, String uriDelimiter) {
		m_aWrapped = m_aWrapped.routingSlip(header, uriDelimiter);
		return this;
	}

	public RouteDefinition routingSlip(String header) {
		m_aWrapped = m_aWrapped.routingSlip(header);
		return this;
	}

	public RouteDefinition routingSlip(String header, String uriDelimiter,
			boolean ignoreInvalidEndpoints) {
		m_aWrapped = m_aWrapped.routingSlip(header, uriDelimiter,
				ignoreInvalidEndpoints);
		return this;
	}

	public RouteDefinition routingSlip(String header,
			boolean ignoreInvalidEndpoints) {
		m_aWrapped = m_aWrapped.routingSlip(header, ignoreInvalidEndpoints);
		return this;
	}

	public RoutingSlipDefinition<RouteDefinition> routingSlip(
			Expression expression, String uriDelimiter) {
		return m_aWrapped.routingSlip(expression, uriDelimiter);
	}

	public RoutingSlipDefinition<RouteDefinition> routingSlip(
			Expression expression) {
		return m_aWrapped.routingSlip(expression);
	}

	public ExpressionClause<RoutingSlipDefinition<RouteDefinition>> routingSlip() {
		return m_aWrapped.routingSlip();
	}

	public DynamicRouterDefinition<RouteDefinition> dynamicRouter(
			Expression expression) {
		return m_aWrapped.dynamicRouter(expression);
	}

	public ExpressionClause<DynamicRouterDefinition<RouteDefinition>> dynamicRouter() {
		return m_aWrapped.dynamicRouter();
	}

	public SamplingDefinition sample() {
		return m_aWrapped.sample();
	}

	public SamplingDefinition sample(long samplePeriod, TimeUnit unit) {
		return m_aWrapped.sample(samplePeriod, unit);
	}

	public SamplingDefinition sample(long messageFrequency) {
		return m_aWrapped.sample(messageFrequency);
	}

	public ExpressionClause<SplitDefinition> split() {
		return m_aWrapped.split();
	}

	public SplitDefinition split(Expression expression) {
		return m_aWrapped.split(expression);
	}

	public SplitDefinition split(Expression expression,
			AggregationStrategy aggregationStrategy) {
		return m_aWrapped.split(expression, aggregationStrategy);
	}

	public ExpressionClause<ResequenceDefinition> resequence() {
		return m_aWrapped.resequence();
	}

	public ResequenceDefinition resequence(Expression expression) {
		return m_aWrapped.resequence(expression);
	}

	public ExpressionClause<AggregateDefinition> aggregate() {
		return m_aWrapped.aggregate();
	}

	public ExpressionClause<AggregateDefinition> aggregate(
			AggregationStrategy aggregationStrategy) {
		return m_aWrapped.aggregate(aggregationStrategy);
	}

	public AggregateDefinition aggregate(Expression correlationExpression) {
		return m_aWrapped.aggregate(correlationExpression);
	}

	public AggregateDefinition aggregate(Expression correlationExpression,
			AggregationStrategy aggregationStrategy) {
		return m_aWrapped.aggregate(correlationExpression, aggregationStrategy);
	}

	public DelayDefinition delay(Expression delay) {
		return m_aWrapped.delay(delay);
	}

	public ExpressionClause<DelayDefinition> delay() {
		return m_aWrapped.delay();
	}

	public DelayDefinition delay(long delay) {
		return m_aWrapped.delay(delay);
	}

	public ThrottleDefinition throttle(long maximumRequestCount) {
		return m_aWrapped.throttle(maximumRequestCount);
	}

	public ThrottleDefinition throttle(Expression maximumRequestCount) {
		return m_aWrapped.throttle(maximumRequestCount);
	}

	public ExpressionClause<LoopDefinition> loop() {
		return m_aWrapped.loop();
	}

	public LoopDefinition loop(Expression expression) {
		return m_aWrapped.loop(expression);
	}

	public LoopDefinition loopDoWhile(Predicate predicate) {
		return m_aWrapped.loopDoWhile(predicate);
	}

	public LoopDefinition loop(int count) {
		return m_aWrapped.loop(count);
	}

	public RouteDefinition throwException(Exception exception) {
		m_aWrapped = m_aWrapped.throwException(exception);
		return this;
	}

	public RouteDefinition throwException(Class<? extends Exception> type,
			String message) {
		m_aWrapped = m_aWrapped.throwException(type, message);
		return this;
	}

	public RouteDefinition markRollbackOnly() {
		m_aWrapped = m_aWrapped.markRollbackOnly();
		return this;
	}

	public RouteDefinition markRollbackOnlyLast() {
		m_aWrapped = m_aWrapped.markRollbackOnlyLast();
		return this;
	}

	public RouteDefinition rollback() {
		m_aWrapped = m_aWrapped.rollback();
		return this;
	}

	public RouteDefinition rollback(String message) {
		m_aWrapped = m_aWrapped.rollback(message);
		return this;
	}

	public WireTapDefinition<RouteDefinition> wireTap(Endpoint endpoint) {
		return m_aWrapped.wireTap(endpoint);
	}

	public WireTapDefinition<RouteDefinition> wireTap(String uri) {
		return m_aWrapped.wireTap(uri);
	}

	public WireTapDefinition<RouteDefinition> wireTap(String uri,
			ExecutorService executorService) {
		return m_aWrapped.wireTap(uri, executorService);
	}

	public WireTapDefinition<RouteDefinition> wireTap(String uri,
			String executorServiceRef) {
		return m_aWrapped.wireTap(uri, executorServiceRef);
	}

	public WireTapDefinition<RouteDefinition> wireTap(String uri,
			Expression body) {
		return m_aWrapped.wireTap(uri, body);
	}

	public WireTapDefinition<RouteDefinition> wireTap(String uri, boolean copy) {
		return m_aWrapped.wireTap(uri, copy);
	}

	public WireTapDefinition<RouteDefinition> wireTap(String uri, boolean copy,
			Expression body) {
		return m_aWrapped.wireTap(uri, copy, body);
	}

	public WireTapDefinition<RouteDefinition> wireTap(String uri,
			Processor processor) {
		return m_aWrapped.wireTap(uri, processor);
	}

	public WireTapDefinition<RouteDefinition> wireTap(String uri, boolean copy,
			Processor processor) {
		return m_aWrapped.wireTap(uri, copy, processor);
	}

	public RouteDefinition stop() {
		m_aWrapped = m_aWrapped.stop();
		return this;
	}

	public OnExceptionDefinition onException(
			Class<? extends Throwable> exceptionType) {
		return m_aWrapped.onException(exceptionType);
	}

	public OnExceptionDefinition onException(
			Class<? extends Throwable>... exceptions) {
		return m_aWrapped.onException(exceptions);
	}

	public PolicyDefinition policy(Policy policy) {
		return m_aWrapped.policy(policy);
	}

	public PolicyDefinition policy(String ref) {
		return m_aWrapped.policy(ref);
	}

	public TransactedDefinition transacted() {
		return m_aWrapped.transacted();
	}

	public TransactedDefinition transacted(String ref) {
		return m_aWrapped.transacted(ref);
	}

	public RouteDefinition process(Processor processor) {
		m_aWrapped = m_aWrapped.process(processor);
		return this;
	}

	public RouteDefinition process(String ref) {
		m_aWrapped = m_aWrapped.process(ref);
		return this;
	}

	public RouteDefinition processRef(String ref) {
		m_aWrapped = m_aWrapped.processRef(ref);
		return this;
	}

	public RouteDefinition bean(Object bean) {
		m_aWrapped = m_aWrapped.bean(bean);
		return this;
	}

	public RouteDefinition bean(Object bean, String method) {
		m_aWrapped = m_aWrapped.bean(bean, method);
		return this;
	}

	public RouteDefinition bean(Object bean, boolean cache) {
		m_aWrapped = m_aWrapped.bean(bean, cache);
		return this;
	}

	public RouteDefinition bean(Object bean, String method, boolean cache) {
		m_aWrapped = m_aWrapped.bean(bean, method, cache);
		return this;
	}

	public RouteDefinition bean(Class<?> beanType) {
		m_aWrapped = m_aWrapped.bean(beanType);
		return this;
	}

	public RouteDefinition bean(Class<?> beanType, String method) {
		m_aWrapped = m_aWrapped.bean(beanType, method);
		return this;
	}

	public RouteDefinition bean(Class<?> beanType, String method,
			boolean multiParameterArray) {
		m_aWrapped = m_aWrapped.bean(beanType, method, multiParameterArray);
		return this;
	}

	public RouteDefinition bean(Class<?> beanType, String method,
			boolean multiParameterArray, boolean cache) {
		m_aWrapped = m_aWrapped.bean(beanType, method, multiParameterArray, cache);
		return this;
	}

	public RouteDefinition beanRef(String ref) {
		m_aWrapped = m_aWrapped.beanRef(ref);
		return this;
	}

	public RouteDefinition beanRef(String ref, String method) {
		m_aWrapped = m_aWrapped.beanRef(ref, method);
		return this;
	}

	public RouteDefinition beanRef(String ref, boolean cache) {
		m_aWrapped = m_aWrapped.beanRef(ref, cache);
		return this;
	}

	public RouteDefinition beanRef(String ref, String method, boolean cache) {
		m_aWrapped = m_aWrapped.beanRef(ref, method, cache);
		return this;
	}

	public RouteDefinition beanRef(String ref, String method, boolean cache,
			boolean multiParameterArray) {
		m_aWrapped = m_aWrapped.beanRef(ref, method, cache, multiParameterArray);
		return this;
	}

	public ExpressionClause<ProcessorDefinition<RouteDefinition>> setBody() {
		return m_aWrapped.setBody();
	}

	public RouteDefinition setBody(Expression expression) {
		m_aWrapped = m_aWrapped.setBody(expression);
		return this;
	}

	public RouteDefinition transform(Expression expression) {
		m_aWrapped = m_aWrapped.transform(expression);
		return this;
	}

	public ExpressionClause<ProcessorDefinition<RouteDefinition>> transform() {
		return m_aWrapped.transform();
	}

	public RouteDefinition script(Expression expression) {
		m_aWrapped = m_aWrapped.script(expression);
		return this;
	}

	public ExpressionClause<ProcessorDefinition<RouteDefinition>> script() {
		return m_aWrapped.script();
	}

	public RouteDefinition setFaultBody(Expression expression) {
		m_aWrapped = m_aWrapped.setFaultBody(expression);
		return this;
	}

	public ExpressionClause<ProcessorDefinition<RouteDefinition>> setHeader(
			String name) {
		return m_aWrapped.setHeader(name);
	}

	public RouteDefinition setHeader(String name, Expression expression) {
		m_aWrapped = m_aWrapped.setHeader(name, expression);
		return this;
	}

	public ExpressionClause<ProcessorDefinition<RouteDefinition>> setOutHeader(
			String name) {
		return m_aWrapped.setOutHeader(name);
	}

	public RouteDefinition setOutHeader(String name, Expression expression) {
		m_aWrapped = m_aWrapped.setOutHeader(name, expression);
		return this;
	}

	public RouteDefinition setFaultHeader(String name, Expression expression) {
		m_aWrapped = m_aWrapped.setFaultHeader(name, expression);
		return this;
	}

	public RouteDefinition setProperty(String name, Expression expression) {
		m_aWrapped = m_aWrapped.setProperty(name, expression);
		return this;
	}

	public ExpressionClause<ProcessorDefinition<RouteDefinition>> setProperty(
			String name) {
		return m_aWrapped.setProperty(name);
	}

	public RouteDefinition removeHeader(String name) {
		m_aWrapped = m_aWrapped.removeHeader(name);
		return this;
	}

	public RouteDefinition removeHeaders(String pattern) {
		m_aWrapped = m_aWrapped.removeHeaders(pattern);
		return this;
	}

	public RouteDefinition removeHeaders(String pattern,
			String... excludePatterns) {
		m_aWrapped = m_aWrapped.removeHeaders(pattern, excludePatterns);
		return this;
	}

	public RouteDefinition removeFaultHeader(String name) {
		m_aWrapped = m_aWrapped.removeFaultHeader(name);
		return this;
	}

	public RouteDefinition removeProperty(String name) {
		m_aWrapped = m_aWrapped.removeProperty(name);
		return this;
	}

	public RouteDefinition removeProperties(String pattern) {
		m_aWrapped = m_aWrapped.removeProperties(pattern);
		return this;
	}

	public RouteDefinition removeProperties(String pattern,
			String... excludePatterns) {
		m_aWrapped = m_aWrapped.removeProperties(pattern, excludePatterns);
		return this;
	}

	public RouteDefinition convertBodyTo(Class<?> type) {
		m_aWrapped = m_aWrapped.convertBodyTo(type);
		return this;
	}

	public RouteDefinition convertBodyTo(Class<?> type, String charset) {
		m_aWrapped = m_aWrapped.convertBodyTo(type, charset);
		return this;
	}

	public RouteDefinition sort(Expression expression) {
		m_aWrapped = m_aWrapped.sort(expression);
		return this;
	}

	public <T> RouteDefinition sort(Expression expression,
			Comparator<T> comparator) {
		m_aWrapped = m_aWrapped.sort(expression, comparator);
		return this;
	}

	public <T> ExpressionClause<SortDefinition<T>> sort() {
		return m_aWrapped.sort();
	}

	public RouteDefinition enrich(String resourceUri) {
		m_aWrapped = m_aWrapped.enrich(resourceUri);
		return this;
	}

	public RouteDefinition enrich(String resourceUri,
			AggregationStrategy aggregationStrategy) {
		m_aWrapped = m_aWrapped.enrich(resourceUri, aggregationStrategy);
		return this;
	}

	public RouteDefinition enrich(String resourceUri,
			AggregationStrategy aggregationStrategy,
			boolean aggregateOnException) {
		m_aWrapped = m_aWrapped.enrich(resourceUri, aggregationStrategy,
				aggregateOnException);
		return this;
	}

	public RouteDefinition enrich(String resourceUri,
			AggregationStrategy aggregationStrategy,
			boolean aggregateOnException, boolean shareUnitOfWork) {
		m_aWrapped = m_aWrapped.enrich(resourceUri, aggregationStrategy,
				aggregateOnException, shareUnitOfWork);
		return this;
	}

	public RouteDefinition enrichRef(String resourceRef,
			String aggregationStrategyRef) {
		m_aWrapped = m_aWrapped.enrichRef(resourceRef, aggregationStrategyRef);
		return this;
	}

	public RouteDefinition enrichRef(String resourceRef,
			String aggregationStrategyRef, boolean aggregateOnException) {
		m_aWrapped = m_aWrapped.enrichRef(resourceRef, aggregationStrategyRef,
				aggregateOnException);
		return this;
	}

	public RouteDefinition enrichRef(String resourceRef,
			String aggregationStrategyRef, boolean aggregateOnException,
			boolean shareUnitOfWork) {
		m_aWrapped = m_aWrapped.enrichRef(resourceRef, aggregationStrategyRef,
				aggregateOnException, shareUnitOfWork);
		return this;
	}

	public ExpressionClause<EnrichDefinition> enrich() {
		return m_aWrapped.enrich();
	}

	public RouteDefinition pollEnrich(String resourceUri) {
		m_aWrapped = m_aWrapped.pollEnrich(resourceUri);
		return this;
	}

	public RouteDefinition pollEnrich(String resourceUri,
			AggregationStrategy aggregationStrategy) {
		m_aWrapped = m_aWrapped.pollEnrich(resourceUri, aggregationStrategy);
		return this;
	}

	public RouteDefinition pollEnrich(String resourceUri, long timeout,
			AggregationStrategy aggregationStrategy) {
		m_aWrapped = m_aWrapped.pollEnrich(resourceUri, timeout, aggregationStrategy);
		return this;
	}

	public RouteDefinition pollEnrich(String resourceUri, long timeout,
			AggregationStrategy aggregationStrategy,
			boolean aggregateOnException) {
		m_aWrapped = m_aWrapped.pollEnrich(resourceUri, timeout, aggregationStrategy,
				aggregateOnException);
		return this;
	}

	public RouteDefinition pollEnrich(String resourceUri, long timeout) {
		m_aWrapped = m_aWrapped.pollEnrich(resourceUri, timeout);
		return this;
	}

	public RouteDefinition pollEnrichRef(String resourceRef, long timeout,
			String aggregationStrategyRef) {
		m_aWrapped = m_aWrapped.pollEnrichRef(resourceRef, timeout,
				aggregationStrategyRef);
		return this;
	}

	public RouteDefinition pollEnrichRef(String resourceRef, long timeout,
			String aggregationStrategyRef, boolean aggregateOnException) {
		m_aWrapped = m_aWrapped.pollEnrichRef(resourceRef, timeout,
				aggregationStrategyRef, aggregateOnException);
		return this;
	}

	public RouteDefinition pollEnrich(Expression expression, long timeout,
			String aggregationStrategyRef, boolean aggregateOnException) {
		m_aWrapped = m_aWrapped.pollEnrich(expression, timeout,
				aggregationStrategyRef, aggregateOnException);
		return this;
	}

	public ExpressionClause<PollEnrichDefinition> pollEnrich() {
		return m_aWrapped.pollEnrich();
	}

	public OnCompletionDefinition onCompletion() {
		return m_aWrapped.onCompletion();
	}

	public DataFormatClause<ProcessorDefinition<RouteDefinition>> unmarshal() {
		return m_aWrapped.unmarshal();
	}

	public RouteDefinition unmarshal(DataFormatDefinition dataFormatType) {
		m_aWrapped = m_aWrapped.unmarshal(dataFormatType);
		return this;
	}

	public RouteDefinition unmarshal(DataFormat dataFormat) {
		m_aWrapped = m_aWrapped.unmarshal(dataFormat);
		return this;
	}

	public RouteDefinition unmarshal(String dataTypeRef) {
		m_aWrapped = m_aWrapped.unmarshal(dataTypeRef);
		return this;
	}

	public DataFormatClause<ProcessorDefinition<RouteDefinition>> marshal() {
		return m_aWrapped.marshal();
	}

	public RouteDefinition marshal(DataFormatDefinition dataFormatType) {
		m_aWrapped = m_aWrapped.marshal(dataFormatType);
		return this;
	}

	public RouteDefinition marshal(DataFormat dataFormat) {
		m_aWrapped = m_aWrapped.marshal(dataFormat);
		return this;
	}

	public RouteDefinition marshal(String dataTypeRef) {
		m_aWrapped = m_aWrapped.marshal(dataTypeRef);
		return this;
	}

	public RouteDefinition inheritErrorHandler(boolean inheritErrorHandler) {
		m_aWrapped = m_aWrapped.inheritErrorHandler(inheritErrorHandler);
		return this;
	}

	public ProcessorDefinition<?> getParent() {
		return m_aWrapped.getParent();
	}

	public void setParent(ProcessorDefinition<?> parent) {
		m_aWrapped.setParent(parent);
	}

	public List<InterceptStrategy> getInterceptStrategies() {
		return m_aWrapped.getInterceptStrategies();
	}

	public void addInterceptStrategy(InterceptStrategy strategy) {
		m_aWrapped.addInterceptStrategy(strategy);
	}

	public Boolean isInheritErrorHandler() {
		return m_aWrapped.isInheritErrorHandler();
	}

	public void setInheritErrorHandler(Boolean inheritErrorHandler) {
		m_aWrapped.setInheritErrorHandler(inheritErrorHandler);
	}

	public Map<QName, Object> getOtherAttributes() {
		return m_aWrapped.getOtherAttributes();
	}

	public void setOtherAttributes(Map<QName, Object> otherAttributes) {
		m_aWrapped.setOtherAttributes(otherAttributes);
	}

	public String getLabel() {
		return m_aWrapped.getLabel();
	}

	//-------------------------------------------------------------------------
	private CamelRouteGroup m_aParent = null;

	//-------------------------------------------------------------------------
	private RouteDefinition m_aWrapped = null;

	//-------------------------------------------------------------------------
	private String m_sRouteID = null;
}