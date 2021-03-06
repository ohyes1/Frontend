package edu.zuo.pegraph.datastructure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import soot.Immediate;
import soot.Local;
import soot.SootMethod;
import soot.Value;
import soot.jimple.AnyNewExpr;
import soot.jimple.ArrayRef;
import soot.jimple.ClassConstant;
import soot.jimple.ConcreteRef;
import soot.jimple.Constant;
import soot.jimple.FieldRef;
import soot.jimple.InstanceFieldRef;
import soot.jimple.InvokeExpr;
import soot.jimple.NewArrayExpr;
import soot.jimple.NewExpr;
import soot.jimple.NewMultiArrayExpr;
import soot.jimple.StaticFieldRef;
import soot.jimple.StringConstant;

public class PegIntra_block {
	
//	private SootMethod soot_method;
	
	//--formal parameters
	private Local formal_callee = null;
	
	private List<Local> formal_paras = new ArrayList<Local>();
	
	//--formal return: could be {Local, StringConstant, ClassConstant}
	private Immediate formal_return = null;

	//--call sites
	private Map callSites = new HashMap<InvokeExpr, CallSite>();
	
	
	//--edges
	private Map<Local, HashSet<Local>> local2Local = new HashMap<Local, HashSet<Local>>();
	
	//obj could be {StringConstant, ClassConstant, NewExpr, NewArrayExpr, NewMultiArrayExpr, NewInstanceInvoke(which is special invokeExpr)}
	private Map<Value, HashSet<Local>> obj2Local = new HashMap<Value, HashSet<Local>>();

	//ref could be {InstanceFieldRef or StaticFieldRef or ArrayRef}
	private Map<ConcreteRef, HashSet<Local>> ref2Local = new HashMap<ConcreteRef, HashSet<Local>>();
	
	//ref could be {InstanceFieldRef or StaticFieldRef or ArrayRef} 
	private Map<Local, HashSet<ConcreteRef>> local2Ref = new HashMap<Local, HashSet<ConcreteRef>>();
	
	//const could be {StringConstant or ClassConstant}, ref could be {InstanceFieldRef or StaticFieldRef or ArrayRef} 
	private Map<Constant, HashSet<ConcreteRef>> const2Ref = new HashMap<Constant, HashSet<ConcreteRef>>();
	


	public PegIntra_block(){
		
	}
	
	
	public void exportIntraGraph(String file_name){
		//TODO
		
	}
	
	public String toString(){
		StringBuilder builder = new StringBuilder();
		
		//--method signature
		
		//--formal parameters
		
		//--formal return
		
		//--call sites
		
		//--edges
		//local2local: Assign
		
		//obj2local: New
		for(Value v: this.obj2Local.keySet()){
			if(v instanceof StringConstant){
				
			}
			else if(v instanceof ClassConstant){
				
			}
			else if(v instanceof AnyNewExpr){
				assert(v instanceof NewExpr || v instanceof NewArrayExpr || v instanceof NewMultiArrayExpr);
				
			}
			else if(v instanceof InvokeExpr){
				
			}
			else{
				System.err.println("obj type error!!!");
			}
		}
		
		//ref2local
		for(ConcreteRef ref: this.ref2Local.keySet()){
			if(ref instanceof InstanceFieldRef){//Load
				
			}
			else if (ref instanceof StaticFieldRef){//Assign
				
			}
			else if (ref instanceof ArrayRef){//Load
				
			}
			else{
				System.err.println("ref type error!!!");
			}
		}
		
		//local2ref
		for(Local local: this.local2Ref.keySet()){
			HashSet<ConcreteRef> refs = this.local2Ref.get(local);
			for(ConcreteRef ref: refs){
				if(ref instanceof InstanceFieldRef){//Store
					
				}
				else if(ref instanceof StaticFieldRef){//Assign
					
				}
				else if (ref instanceof ArrayRef){//Store
					
				}
				else{
					System.err.println("ref type error!!!");
				}
			}
		}
		
		//const2ref: New & Store
		for(Constant cons: this.const2Ref.keySet()){
			HashSet<ConcreteRef> refs = this.const2Ref.get(cons);
			for(ConcreteRef ref: refs){
				if(ref instanceof InstanceFieldRef){//New & Store: (x.f = constant) <==> (x <-Store[f]- tmp <-New- constant)
					
				}
				else if(ref instanceof StaticFieldRef){//New: (X.f = constant) <==> (f <-New- constant)
					
				}
				else if (ref instanceof ArrayRef){//New & Store: (array[*] = constant) <==> (array <-Store[E]- tmp <-New- constant)
					
				}
				else{
					System.err.println("ref type error!!!");
				}
			}
		}
		
		
		return builder.toString();
	}


	public void addJavaClassObj2Local(InvokeExpr newie, Local lhs) {
		// TODO Auto-generated method stub
		addObj2Local(newie, lhs);
	}


	public CallSite createCallSite(InvokeExpr ie) {
		// TODO Auto-generated method stub
		CallSite callsite = new CallSite();
		this.callSites.put(ie, callsite);
		return callsite;
	}



	public void setFormalReturn(Immediate v) {
		// TODO Auto-generated method stub
		if(v instanceof Local){
			
		}
		else if (v instanceof StringConstant){
			
			
		}
		else if(v instanceof ClassConstant){
			
		}
		else{
			System.err.println("error!!!");
		}
		this.formal_return = v;
	}
	
	public void setFormalCallee(Local lhs) {
		this.formal_callee = lhs;
	}

	public void addFormalParameter(Local lhs) {
		// TODO Auto-generated method stub
		this.formal_paras.add(lhs);
	}


	public void addLocal2ArrayRef(Local rhs, ArrayRef lhs) {
		// TODO Auto-generated method stub
		addLocal2Ref(rhs, lhs);
	}


	public void addStringConst2ArrayRef(StringConstant rhs, ArrayRef lhs) {
		// TODO Auto-generated method stub
		addConst2Ref(rhs, lhs);
	}


	public void addClassConst2ArrayRef(ClassConstant rhs, ArrayRef lhs) {
		// TODO Auto-generated method stub
		addConst2Ref(rhs, lhs);
	}


	public void addLocal2FieldRef(Local rhs, FieldRef lhs) {
		// TODO Auto-generated method stub
		assert(lhs instanceof InstanceFieldRef || lhs instanceof StaticFieldRef);
		addLocal2Ref(rhs, lhs);
	}


	public void addStringConst2FieldRef(StringConstant rhs, FieldRef lhs) {
		// TODO Auto-generated method stub
		assert(lhs instanceof InstanceFieldRef || lhs instanceof StaticFieldRef);
		addConst2Ref(rhs, lhs);
	}


	public void addClassConst2FieldRef(ClassConstant rhs, FieldRef lhs) {
		assert(lhs instanceof InstanceFieldRef || lhs instanceof StaticFieldRef);
		addConst2Ref(rhs, lhs);
	}


	public void addLocal2Local(Local rhs, Local lhs) {
		// TODO Auto-generated method stub
		if(this.local2Local.containsKey(rhs)){
			this.local2Local.get(rhs).add(lhs);
		}
		else{
			HashSet<Local> set = new HashSet<Local>();
			set.add(lhs);
			this.local2Local.put(rhs, set);
		}
	}


	public void addStringConst2Local(StringConstant rhs, Local lhs) {
		// TODO Auto-generated method stub
		addObj2Local(rhs, lhs);
	}


	public void addClassConst2Local(ClassConstant rhs, Local lhs) {
		// TODO Auto-generated method stub
		addObj2Local(rhs, lhs);
	}


	public void addNewExpr2Local(NewExpr rhs, Local lhs) {
		// TODO Auto-generated method stub
		addObj2Local(rhs, lhs);
	}


	public void addNewArrayExpr2Local(NewArrayExpr rhs, Local lhs) {
		// TODO Auto-generated method stub
		addObj2Local(rhs, lhs);
	}


	public void addNewMultiArrayExpr2Local(NewMultiArrayExpr rhs, Local lhs) {
		// TODO Auto-generated method stub
		addObj2Local(rhs, lhs);
	}


	public void addField2Local(FieldRef rhs, Local lhs) {
		assert(rhs instanceof InstanceFieldRef || rhs instanceof StaticFieldRef);
		addRef2Local(rhs, lhs);
	}


	public void addArrayRef2Local(ArrayRef rhs, Local lhs) {
		// TODO Auto-generated method stub
		addRef2Local(rhs, lhs);
	}
	
	public void addObj2Local(Value obj, Local l){
		if(this.obj2Local.containsKey(obj)){
			this.obj2Local.get(obj).add(l);
		}
		else{
			HashSet<Local> set = new HashSet<Local>();
			set.add(l);
			this.obj2Local.put(obj, set);
		}
	}
	
//	public void addStatic2Local(StaticFieldRef sfield, Local l){
//		if(this.static2Local.containsKey(sfield)){
//			this.static2Local.get(sfield).add(l);
//		}
//		else{
//			HashSet<Local> set = new HashSet<Local>();
//			set.add(l);
//			this.static2Local.put(sfield, set);
//		}
//	}
	
	public void addRef2Local(ConcreteRef ref, Local l){
		assert(ref instanceof InstanceFieldRef || ref instanceof ArrayRef || ref instanceof StaticFieldRef);
		if(this.ref2Local.containsKey(ref)){
			this.ref2Local.get(ref).add(l);
		}
		else{
			HashSet<Local> set = new HashSet<Local>();
			set.add(l);
			this.ref2Local.put(ref, set);
		}
	}
	
	public void addLocal2Ref(Local l, ConcreteRef ref){
		if(this.local2Ref.containsKey(l)){
			this.local2Ref.get(l).add(ref);
		}
		else{
			HashSet<ConcreteRef> set = new HashSet<ConcreteRef>();
			set.add(ref);
			this.local2Ref.put(l, set);
		}
	}
	
//	public void addLocal2Static(Local l, StaticFieldRef sfield){
//		if(this.local2Static.containsKey(l)){
//			this.local2Static.get(l).add(sfield);
//		}
//		else{
//			HashSet<StaticFieldRef> set = new HashSet<StaticFieldRef>();
//			set.add(sfield);
//			this.local2Static.put(l, set);
//		}
//	}

	public void addConst2Ref(Constant cons, ConcreteRef ref){
		if(this.const2Ref.containsKey(cons)){
			this.const2Ref.get(cons).add(ref);
		}
		else{
			HashSet<ConcreteRef> set = new HashSet<ConcreteRef>();
			set.add(ref);
			this.const2Ref.put(cons, set);
		}
	}
	
	
	

	
	public Local getFormal_callee() {
		return formal_callee;
	}


	public List<Local> getFormal_paras() {
		return formal_paras;
	}


	public Immediate getFormal_return() {
		return formal_return;
	}


	public Map getCallSites() {
		return callSites;
	}


	public Map<Local, HashSet<Local>> getLocal2Local() {
		return local2Local;
	}


	public Map<Value, HashSet<Local>> getObj2Local() {
		return obj2Local;
	}


	public Map<ConcreteRef, HashSet<Local>> getRef2Local() {
		return ref2Local;
	}


	public Map<Local, HashSet<ConcreteRef>> getLocal2Ref() {
		return local2Ref;
	}


	public Map<Constant, HashSet<ConcreteRef>> getConst2Ref() {
		return const2Ref;
	}





	public class CallSite{
		
		private Immediate actual_callee = null;
		
		//arg could be {Local or StringConstant or ClassConstant}
		private List<Immediate> actual_args = new ArrayList<Immediate>();
		
		private Local actural_return = null;
		
		public CallSite(){
			
		}

		public void setReceiver(Immediate base) {
			// TODO Auto-generated method stub
			this.actual_callee = base;
		}

		public void setActualReturn(Local lhs) {
			// TODO Auto-generated method stub
			this.actural_return = lhs;
		}

		public void addArg(Immediate arg) {
			// TODO Auto-generated method stub
			if(arg instanceof Local){

			}
			else if(arg instanceof StringConstant){
				
			}
			else if(arg instanceof ClassConstant){
				
			}
			else{
				System.err.println("error!!!");
			}
			this.actual_args.add(arg);
		}

		public Immediate getActual_callee() {
			return actual_callee;
		}

		public List<Immediate> getActual_args() {
			return actual_args;
		}

		public Local getActural_return() {
			return actural_return;
		}
		
		
	}
	
}
