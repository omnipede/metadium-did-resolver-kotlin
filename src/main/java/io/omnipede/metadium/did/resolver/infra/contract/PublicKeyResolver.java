package io.omnipede.metadium.did.resolver.infra.contract;

import io.reactivex.Flowable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.DynamicBytes;
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
public class PublicKeyResolver extends Contract {
    public static final String BINARY = "Bin file was not provided";

    public static final String FUNC_REMOVEPUBLICKEYDELEGATED = "removePublicKeyDelegated";

    public static final String FUNC_ADDPUBLICKEYDELEGATED = "addPublicKeyDelegated";

    public static final String FUNC_ADDPUBLICKEY = "addPublicKey";

    public static final String FUNC_SIGNATURETIMEOUT = "signatureTimeout";

    public static final String FUNC_GETPUBLICKEY = "getPublicKey";

    public static final String FUNC_ISSIGNED = "isSigned";

    public static final String FUNC_REMOVEPUBLICKEY = "removePublicKey";

    public static final String FUNC_NAME = "NAME";

    public static final String FUNC_CALCULATEADDRESS = "calculateAddress";

    public static final Event PUBLICKEYADDED_EVENT = new Event("PublicKeyAdded", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Uint256>(true) {}, new TypeReference<DynamicBytes>() {}, new TypeReference<Bool>() {}));
    ;

    public static final Event PUBLICKEYREMOVED_EVENT = new Event("PublicKeyRemoved", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Uint256>(true) {}, new TypeReference<Bool>() {}));
    ;

    @Deprecated
    protected PublicKeyResolver(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected PublicKeyResolver(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected PublicKeyResolver(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected PublicKeyResolver(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteFunctionCall<TransactionReceipt> removePublicKeyDelegated(String associatedAddress, BigInteger v, byte[] r, byte[] s, BigInteger timestamp) {
        final Function function = new Function(
                FUNC_REMOVEPUBLICKEYDELEGATED, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, associatedAddress), 
                new org.web3j.abi.datatypes.generated.Uint8(v), 
                new org.web3j.abi.datatypes.generated.Bytes32(r), 
                new org.web3j.abi.datatypes.generated.Bytes32(s), 
                new org.web3j.abi.datatypes.generated.Uint256(timestamp)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> addPublicKeyDelegated(String associatedAddress, byte[] publicKey, BigInteger v, byte[] r, byte[] s, BigInteger timestamp) {
        final Function function = new Function(
                FUNC_ADDPUBLICKEYDELEGATED, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, associatedAddress), 
                new org.web3j.abi.datatypes.DynamicBytes(publicKey), 
                new org.web3j.abi.datatypes.generated.Uint8(v), 
                new org.web3j.abi.datatypes.generated.Bytes32(r), 
                new org.web3j.abi.datatypes.generated.Bytes32(s), 
                new org.web3j.abi.datatypes.generated.Uint256(timestamp)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> addPublicKey(byte[] publicKey) {
        final Function function = new Function(
                FUNC_ADDPUBLICKEY, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicBytes(publicKey)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<BigInteger> signatureTimeout() {
        final Function function = new Function(FUNC_SIGNATURETIMEOUT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<byte[]> getPublicKey(String addr) {
        final Function function = new Function(FUNC_GETPUBLICKEY, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, addr)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicBytes>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
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

    public RemoteFunctionCall<TransactionReceipt> removePublicKey() {
        final Function function = new Function(
                FUNC_REMOVEPUBLICKEY, 
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

    public RemoteFunctionCall<String> calculateAddress(byte[] publicKey) {
        final Function function = new Function(FUNC_CALCULATEADDRESS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicBytes(publicKey)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public List<PublicKeyAddedEventResponse> getPublicKeyAddedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(PUBLICKEYADDED_EVENT, transactionReceipt);
        ArrayList<PublicKeyAddedEventResponse> responses = new ArrayList<PublicKeyAddedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            PublicKeyAddedEventResponse typedResponse = new PublicKeyAddedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.addr = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.ein = (BigInteger) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.publicKey = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.delegated = (Boolean) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<PublicKeyAddedEventResponse> publicKeyAddedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new io.reactivex.functions.Function<Log, PublicKeyAddedEventResponse>() {
            @Override
            public PublicKeyAddedEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(PUBLICKEYADDED_EVENT, log);
                PublicKeyAddedEventResponse typedResponse = new PublicKeyAddedEventResponse();
                typedResponse.log = log;
                typedResponse.addr = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.ein = (BigInteger) eventValues.getIndexedValues().get(1).getValue();
                typedResponse.publicKey = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.delegated = (Boolean) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<PublicKeyAddedEventResponse> publicKeyAddedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(PUBLICKEYADDED_EVENT));
        return publicKeyAddedEventFlowable(filter);
    }

    public List<PublicKeyRemovedEventResponse> getPublicKeyRemovedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(PUBLICKEYREMOVED_EVENT, transactionReceipt);
        ArrayList<PublicKeyRemovedEventResponse> responses = new ArrayList<PublicKeyRemovedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            PublicKeyRemovedEventResponse typedResponse = new PublicKeyRemovedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.addr = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.ein = (BigInteger) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.delegated = (Boolean) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<PublicKeyRemovedEventResponse> publicKeyRemovedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new io.reactivex.functions.Function<Log, PublicKeyRemovedEventResponse>() {
            @Override
            public PublicKeyRemovedEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(PUBLICKEYREMOVED_EVENT, log);
                PublicKeyRemovedEventResponse typedResponse = new PublicKeyRemovedEventResponse();
                typedResponse.log = log;
                typedResponse.addr = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.ein = (BigInteger) eventValues.getIndexedValues().get(1).getValue();
                typedResponse.delegated = (Boolean) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<PublicKeyRemovedEventResponse> publicKeyRemovedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(PUBLICKEYREMOVED_EVENT));
        return publicKeyRemovedEventFlowable(filter);
    }

    @Deprecated
    public static PublicKeyResolver load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new PublicKeyResolver(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static PublicKeyResolver load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new PublicKeyResolver(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static PublicKeyResolver load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new PublicKeyResolver(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static PublicKeyResolver load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new PublicKeyResolver(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static class PublicKeyAddedEventResponse extends BaseEventResponse {
        public String addr;

        public BigInteger ein;

        public byte[] publicKey;

        public Boolean delegated;
    }

    public static class PublicKeyRemovedEventResponse extends BaseEventResponse {
        public String addr;

        public BigInteger ein;

        public Boolean delegated;
    }
}
