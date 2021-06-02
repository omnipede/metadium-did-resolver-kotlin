package io.omnipede.metadium.did.resolver.infra.contract;

import io.reactivex.Flowable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.BaseEventResponse;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 1.4.1.
 */
@SuppressWarnings("rawtypes")
public class ServiceKeyResolver extends Contract {
    public static final String BINARY = "Bin file was not provided";

    public static final String FUNC_REMOVEKEYDELEGATED = "removeKeyDelegated";

    public static final String FUNC_SIGNATURETIMEOUT = "signatureTimeout";

    public static final String FUNC_REMOVEKEYSDELEGATED = "removeKeysDelegated";

    public static final String FUNC_REMOVEKEY = "removeKey";

    public static final String FUNC_ISKEYFOR = "isKeyFor";

    public static final String FUNC_ISSIGNED = "isSigned";

    public static final String FUNC_ADDKEY = "addKey";

    public static final String FUNC_ADDKEYDELEGATED = "addKeyDelegated";

    public static final String FUNC_GETKEYS = "getKeys";

    public static final String FUNC_REMOVEKEYS = "removeKeys";

    public static final String FUNC_NAME = "NAME";

    public static final String FUNC_GETSYMBOL = "getSymbol";

    public static final Event KEYADDED_EVENT = new Event("KeyAdded", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Uint256>(true) {}, new TypeReference<Utf8String>() {}));
    ;

    public static final Event KEYREMOVED_EVENT = new Event("KeyRemoved", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Uint256>(true) {}));
    ;

    @Deprecated
    protected ServiceKeyResolver(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected ServiceKeyResolver(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected ServiceKeyResolver(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected ServiceKeyResolver(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteFunctionCall<TransactionReceipt> removeKeyDelegated(String associatedAddress, String key, BigInteger v, byte[] r, byte[] s, BigInteger timestamp) {
        final Function function = new Function(
                FUNC_REMOVEKEYDELEGATED, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, associatedAddress), 
                new org.web3j.abi.datatypes.Address(160, key), 
                new org.web3j.abi.datatypes.generated.Uint8(v), 
                new org.web3j.abi.datatypes.generated.Bytes32(r), 
                new org.web3j.abi.datatypes.generated.Bytes32(s), 
                new org.web3j.abi.datatypes.generated.Uint256(timestamp)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<BigInteger> signatureTimeout() {
        final Function function = new Function(FUNC_SIGNATURETIMEOUT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> removeKeysDelegated(String associatedAddress, BigInteger v, byte[] r, byte[] s, BigInteger timestamp) {
        final Function function = new Function(
                FUNC_REMOVEKEYSDELEGATED, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, associatedAddress), 
                new org.web3j.abi.datatypes.generated.Uint8(v), 
                new org.web3j.abi.datatypes.generated.Bytes32(r), 
                new org.web3j.abi.datatypes.generated.Bytes32(s), 
                new org.web3j.abi.datatypes.generated.Uint256(timestamp)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> removeKey(String key) {
        final Function function = new Function(
                FUNC_REMOVEKEY, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, key)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<Boolean> isKeyFor(String key, BigInteger ein) {
        final Function function = new Function(FUNC_ISKEYFOR, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, key), 
                new org.web3j.abi.datatypes.generated.Uint256(ein)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<Boolean> isSigned(String _address, byte[] messageHash, BigInteger v, byte[] r, byte[] s) {
        final Function function = new Function(FUNC_ISSIGNED, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _address), 
                new org.web3j.abi.datatypes.generated.Bytes32(messageHash), 
                new org.web3j.abi.datatypes.generated.Uint8(v), 
                new org.web3j.abi.datatypes.generated.Bytes32(r), 
                new org.web3j.abi.datatypes.generated.Bytes32(s)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<TransactionReceipt> addKey(String key, String symbol) {
        final Function function = new Function(
                FUNC_ADDKEY, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, key), 
                new org.web3j.abi.datatypes.Utf8String(symbol)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> addKeyDelegated(String associatedAddress, String key, String symbol, BigInteger v, byte[] r, byte[] s, BigInteger timestamp) {
        final Function function = new Function(
                FUNC_ADDKEYDELEGATED, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, associatedAddress), 
                new org.web3j.abi.datatypes.Address(160, key), 
                new org.web3j.abi.datatypes.Utf8String(symbol), 
                new org.web3j.abi.datatypes.generated.Uint8(v), 
                new org.web3j.abi.datatypes.generated.Bytes32(r), 
                new org.web3j.abi.datatypes.generated.Bytes32(s), 
                new org.web3j.abi.datatypes.generated.Uint256(timestamp)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<List> getKeys(BigInteger ein) {
        final Function function = new Function(FUNC_GETKEYS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(ein)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Address>>() {}));
        return new RemoteFunctionCall<List>(function,
                new Callable<List>() {
                    @Override
                    @SuppressWarnings("unchecked")
                    public List call() throws Exception {
                        List<Type> result = (List<Type>) executeCallSingleValueReturn(function, List.class);
                        return convertToNative(result);
                    }
                });
    }

    public RemoteFunctionCall<TransactionReceipt> removeKeys() {
        final Function function = new Function(
                FUNC_REMOVEKEYS, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<String> NAME() {
        final Function function = new Function(FUNC_NAME, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<String> getSymbol(String key) {
        final Function function = new Function(FUNC_GETSYMBOL, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, key)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public List<KeyAddedEventResponse> getKeyAddedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(KEYADDED_EVENT, transactionReceipt);
        ArrayList<KeyAddedEventResponse> responses = new ArrayList<KeyAddedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            KeyAddedEventResponse typedResponse = new KeyAddedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.key = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.ein = (BigInteger) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.symbol = (String) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<KeyAddedEventResponse> keyAddedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new io.reactivex.functions.Function<Log, KeyAddedEventResponse>() {
            @Override
            public KeyAddedEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(KEYADDED_EVENT, log);
                KeyAddedEventResponse typedResponse = new KeyAddedEventResponse();
                typedResponse.log = log;
                typedResponse.key = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.ein = (BigInteger) eventValues.getIndexedValues().get(1).getValue();
                typedResponse.symbol = (String) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<KeyAddedEventResponse> keyAddedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(KEYADDED_EVENT));
        return keyAddedEventFlowable(filter);
    }

    public List<KeyRemovedEventResponse> getKeyRemovedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(KEYREMOVED_EVENT, transactionReceipt);
        ArrayList<KeyRemovedEventResponse> responses = new ArrayList<KeyRemovedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            KeyRemovedEventResponse typedResponse = new KeyRemovedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.key = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.ein = (BigInteger) eventValues.getIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<KeyRemovedEventResponse> keyRemovedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new io.reactivex.functions.Function<Log, KeyRemovedEventResponse>() {
            @Override
            public KeyRemovedEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(KEYREMOVED_EVENT, log);
                KeyRemovedEventResponse typedResponse = new KeyRemovedEventResponse();
                typedResponse.log = log;
                typedResponse.key = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.ein = (BigInteger) eventValues.getIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<KeyRemovedEventResponse> keyRemovedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(KEYREMOVED_EVENT));
        return keyRemovedEventFlowable(filter);
    }

    @Deprecated
    public static ServiceKeyResolver load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new ServiceKeyResolver(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static ServiceKeyResolver load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new ServiceKeyResolver(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static ServiceKeyResolver load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new ServiceKeyResolver(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static ServiceKeyResolver load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new ServiceKeyResolver(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static class KeyAddedEventResponse extends BaseEventResponse {
        public String key;

        public BigInteger ein;

        public String symbol;
    }

    public static class KeyRemovedEventResponse extends BaseEventResponse {
        public String key;

        public BigInteger ein;
    }
}
