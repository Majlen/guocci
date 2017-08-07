package cz.cesnet.cloud.occi.infrastructure;

import cz.cesnet.cloud.occi.core.Mixin;

public class ResourceTemplateMixin {
	private Mixin mixin;
	private Mixin parentMixin;

	public ResourceTemplateMixin(Mixin mixin, Mixin parentMixin) {
		this.mixin = mixin;
		this.parentMixin = parentMixin;
	}

	public String getId() {
		return mixin.getTerm();
	}

	public String getTitle() {
		return mixin.getTitle();
	}

	public Mixin getMixin() {
		return mixin;
	}

	public Mixin getParentMixin() {
		return parentMixin;
	}

	public String toString() {
		return getTitle();
	}
}
