/***** Lobxxx Translate Finished ******/
package org.omg.PortableInterceptor;


/**
* org/omg/PortableInterceptor/PolicyFactoryOperations.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from c:/re/workspace/8-2-build-windows-amd64-cygwin/jdk8u45/3627/corba/src/share/classes/org/omg/PortableInterceptor/Interceptors.idl
* Thursday, April 30, 2015 12:42:09 PM PDT
*/


/**
   * Enables policy types to be constructed using 
   * <code>CORBA.ORB.create_policy</code>.
   * <p>
   * A portable ORB service implementation registers an instance of the 
   * <code>PolicyFactory</code> interface during ORB initialization in order 
   * to enable its policy types to be constructed using 
   * <code>CORBA.ORB.create_policy</code>. The POA is required to preserve 
   * any policy which is registered with <code>ORBInitInfo</code> in this 
   * manner.
   *
   * <p>
   *  允许使用<code> CORBA.ORB.create_policy </code>构建策略类型。
   * <p>
   *  便携式ORB服务实现在ORB初始化期间注册<code> PolicyFactory </code>接口的实例,以便使用<code> CORBA.ORB.create_policy </code>来构造
   * 其策略类型。
   *  POA需要保留以这种方式向<code> ORBInitInfo </code>注册的任何策略。
   * 
   * 
   * @see ORBInitInfo#register_policy_factory
   */
public interface PolicyFactoryOperations 
{

  /**
     * Returns an instance of the appropriate interface derived from 
     * <code>CORBA.Policy</code> whose value corresponds to the 
     * specified any. 
     * <p>
     * The ORB calls <code>create_policy</code> on a registered 
     * <code>PolicyFactory</code> instance when 
     * <code>CORBA.ORB.create_policy</code> is called for the 
     * <code>PolicyType</code> under which the <code>PolicyFactory</code> has 
     * been registered. The <code>create_policy</code> operation then 
     * returns an instance of the appropriate interface derived from 
     * <code>CORBA.Policy</code> whose value corresponds to the specified 
     * any. If it cannot, it shall throw an exception as described for 
     * <code>CORBA.ORB.create_policy</code>. 
     * 
     * <p>
     *  返回从<code> CORBA.Policy </code>派生的适当接口的实例,其值对应于指定的any。
     * <p>
     *  当为<code> PolicyType </code>调用<code> CORBA.ORB.create_policy </code>时,ORB在注册的<code> PolicyFactory </code>
     * 实例上调用<code> create_policy </code>已经注册了<code> PolicyFactory </code>。
     * 
     * @param type An int specifying the type of policy being created. 
     * @param value An any containing data with which to construct the 
     *     <code>CORBA.Policy</code>. 
     * @return A <code>CORBA.Policy<code> object of the specified type and 
     *     value.
     */
  org.omg.CORBA.Policy create_policy (int type, org.omg.CORBA.Any value) throws org.omg.CORBA.PolicyError;
} // interface PolicyFactoryOperations
