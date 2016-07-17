package com.thinksns.sociax.t4.android.widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.thinksns.sociax.android.R;
import com.thinksns.sociax.t4.unit.PinyinComparator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ContactListAdapter extends ArrayAdapter<ContactItemInterface> {

	private int resource; // store the resource layout id for 1 row
	private boolean inSearchMode = false;
	protected List<ContactItemInterface> mItems;
	private ContactsSectionIndexer indexer = null;

	public ContactListAdapter(Context _context, int _resource,
			List<ContactItemInterface> _items) {
		super(_context, _resource, _items);
		resource = _resource;
		mItems = _items;
		// need to sort the items array first, then pass it to the indexer
		ContactsSectionIndexer indexer = new ContactsSectionIndexer(_items);
		setIndexer(indexer);

	}

	@Override
	public int getCount() {
		return super.getCount();
	}

	@Override
	public int getViewTypeCount() {
		return 1;
	}


	// get the section textview from row view
	// the section view will only be shown for the first item
	public TextView getSectionTextView(View rowView) {
		TextView sectionTextView = (TextView) rowView
				.findViewById(R.id.sectionTextView);
		return sectionTextView;
	}

	public void showSectionViewIfFirstItem(View rowView,
			ContactItemInterface item, int position) {
		TextView sectionTextView = getSectionTextView(rowView);

		// if in search mode then dun show the section header
		if (inSearchMode) {
			sectionTextView.setVisibility(View.GONE);
		} else {
			// if first item then show the header

			if (indexer.isFirstItemInSection(position)) {

				String sectionTitle = indexer.getSectionTitle(item
						.getItemForIndex());
				if(sectionTitle != null) {
					sectionTextView.setText(sectionTitle);
					sectionTextView.setVisibility(View.VISIBLE);
				}else {
					sectionTextView.setVisibility(View.GONE);
				}

			} else
				sectionTextView.setVisibility(View.GONE);
		}
	}

	// do all the data population for the row here
	// subclass overwrite this to draw more items
	public void populateDataForRow(View parentView, ContactItemInterface item,
			int position) {
		// default just draw the item only
		View infoView = parentView.findViewById(R.id.rl_user);
		TextView nameView = (TextView) infoView.findViewById(R.id.tv_username);
		nameView.setText(item.getItemForIndex());
	}

	// this should be override by subclass if necessary
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		int type = getItemViewType(position);
		ViewGroup parentView;
		if (convertView == null) {
			parentView = new LinearLayout(getContext());
			String inflater = Context.LAYOUT_INFLATER_SERVICE;
			LayoutInflater vi = (LayoutInflater) getContext().getSystemService(
						inflater);
			vi.inflate(resource, parentView, true);

		} else {
			parentView = (LinearLayout) convertView;
		}

			ContactItemInterface item = getItem(position);
			showSectionViewIfFirstItem(parentView, item, position);

			populateDataForRow(parentView, item, position);

		return parentView;

	}

	public boolean isInSearchMode() {
		return inSearchMode;
	}

	public void setInSearchMode(boolean inSearchMode) {
		this.inSearchMode = inSearchMode;
	}

	public ContactsSectionIndexer getIndexer() {
		return indexer;
	}

	public void setIndexer(ContactsSectionIndexer indexer) {
		this.indexer = indexer;
	}

}
